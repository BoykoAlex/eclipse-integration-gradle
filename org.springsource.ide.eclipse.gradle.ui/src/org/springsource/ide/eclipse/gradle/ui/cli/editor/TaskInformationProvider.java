package org.springsource.ide.eclipse.gradle.ui.cli.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.source.ISourceViewer;
import org.gradle.tooling.model.GradleTask;
import org.springsource.ide.eclipse.gradle.core.util.GradleTasksIndex;

public class TaskInformationProvider extends DefaultTextHover implements IInformationProvider {
	
	private GradleTasksIndex tasksIndex;
	
	public TaskInformationProvider(ISourceViewer sourceViewer, GradleTasksIndex tasksIndex) {
		super(sourceViewer);
		this.tasksIndex = tasksIndex;
	}

	@Override
	public IRegion getSubject(ITextViewer textViewer, int offset) {
		IDocument document = textViewer.getDocument();
		try {
			if (!Character.isWhitespace(document.getChar(offset))) {
				int start = offset;
				int length = 1;
				for (int index = offset - 1; index >= 0 && !Character.isWhitespace(document.getChar(index)); index--) {
					start--;
					length++;
				}
				for (int index = offset + 1; index < document.getLength() && !Character.isWhitespace(document.getChar(index)); index++) {
					length++;
				}
				return new Region(start, length);
			}
		} catch (BadLocationException e) {
			// ignore
		}
		return null;
	}

	@Override
	public String getInformation(ITextViewer textViewer, IRegion subject) {
		return getHoverInfo(textViewer, subject);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		try {
			GradleTask task = tasksIndex.getTask(textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength()));
			if (task != null) {
				return task.getDescription();
			}
		} catch (BadLocationException e) {
			// ignore
		}
		return super.getHoverInfo(textViewer, hoverRegion);	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return getSubject(textViewer, offset);
	}

}
