package org.pitest.pitclipse.ui.view.mutations;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.pitest.pitclipse.pitrunner.model.MutationsModel;

public class PitMutationsView extends ViewPart {

	private class ViewUpdater implements Runnable {
		private final MutationsModel mutations;

		private ViewUpdater(MutationsModel mutations) {
			this.mutations = mutations;
		}

		@Override
		public void run() {
			viewer.setInput(mutations);
			viewer.expandAll();
		}
	}

	private static final int TREE_STYLE = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
	private TreeViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, TREE_STYLE);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(MutationsModel.EMPTY_MODEL);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void updateWith(final MutationsModel mutations) {
		Display.getDefault().asyncExec(new ViewUpdater(mutations));
	}

}