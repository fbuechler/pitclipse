package org.pitest.pitclipse.ui.behaviours.steps;

import static org.junit.Assert.assertEquals;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.INSTANCE;
import static org.pitest.pitclipse.ui.util.AssertUtil.assertDoubleEquals;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.pitest.pitclipse.ui.behaviours.Then;
import org.pitest.pitclipse.ui.behaviours.When;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PackageContext;

public class PitclipseSteps {

	public class SelectProject implements Runnable {

		private final String projectName;

		public SelectProject(String projectName) {
			this.projectName = projectName;
		}

		public void run() {
			INSTANCE.getPackageExplorer().selectProject(projectName);
		}

	}

	private static final class SelectPackageRoot implements Runnable {
		private static final class PackageRootSelector implements
				PackageContext {
			private final String projectName;
			private final String packageRoot;

			public PackageRootSelector(String projectName, String packageRoot) {
				this.projectName = projectName;
				this.packageRoot = packageRoot;
			}

			public String getPackageName() {
				return null;
			}

			public String getProjectName() {
				return projectName;
			}

			public String getSourceDir() {
				return packageRoot;
			}
		}

		private final PackageRootSelector context;

		private SelectPackageRoot(String packageRoot, String projectName) {
			context = new PackageRootSelector(projectName, packageRoot);
		}

		public void run() {
			INSTANCE.getPackageExplorer().selectPackageRoot(context);
		}
	}

	private static final class SelectPackage implements Runnable {
		private static final class PackageSelector implements PackageContext {
			private final String projectName;
			private final String packageName;

			public PackageSelector(String projectName, String packageName) {
				this.projectName = projectName;
				this.packageName = packageName;
			}

			public String getPackageName() {
				return packageName;
			}

			public String getProjectName() {
				return projectName;
			}

			public String getSourceDir() {
				return null;
			}
		}

		private final PackageSelector context;

		private SelectPackage(String packageName, String projectName) {
			context = new PackageSelector(projectName, packageName);
		}

		public void run() {
			INSTANCE.getPackageExplorer().selectPackage(context);
		}
	}

	private static final class SelectTestClass implements Runnable {
		private final String testClassName;
		private final String packageName;
		private final String projectName;

		private SelectTestClass(String testClassName, String packageName,
				String projectName) {
			this.testClassName = testClassName;
			this.packageName = packageName;
			this.projectName = projectName;
		}

		public void run() {
			INSTANCE.getPackageExplorer().selectClass(testClassName,
					packageName, projectName);
		}
	}

	@When("test $testClassName in package $packageName is run for project $projectName")
	public void runTest(final String projectName, final String packageName,
			final String testClassName) {
		runPit(new SelectTestClass(testClassName, packageName, projectName));

	}

	private void runPit(Runnable runnable) {
		int retryCount = 20;
		int counter = 0;
		while (counter < retryCount) {
			try {
				runnable.run();
				INSTANCE.getRunMenu().runPit();
				return;
			} catch (TimeoutException te) {
				counter++;
			} catch (WidgetNotFoundException wfne) {
				counter++;
			}
		}

	}

	@Then("a coverage report is generated with $classes classes tested with overall coverage of $totalCoverage and mutation coverage of $mutationCoverage")
	public void coverageReportGenerated(int classes, double totalCoverage,
			double mutationCoverage) {
		INSTANCE.getPitView().waitForUpdate();
		assertEquals(classes, INSTANCE.getPitView().getClassesTested());
		assertDoubleEquals(totalCoverage, INSTANCE.getPitView()
				.getOverallCoverage());
		assertDoubleEquals(mutationCoverage, INSTANCE.getPitView()
				.getMutationCoverage());
	}

	@When("tests in package $packageName are run for project $projectName")
	public void runPackageTest(final String projectName,
			final String packageName) {
		runPit(new SelectPackage(packageName, projectName));
	}

	@When("tests in package root $packageRoot are run for project $projectName")
	public void runPackageRootTest(final String projectName,
			final String packageRoot) {
		runPit(new SelectPackageRoot(packageRoot, projectName));
	}

	@When("tests are run for project $projectName")
	public void runProjectTest(String projectName, String sourceDir) {
		runPit(new SelectProject(projectName));
	}
}
