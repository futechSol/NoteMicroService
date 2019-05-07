package com.bridgelabz.noteMicroService.model;

import java.util.Comparator;

/**
 * comparator for comparing the 2 Note instances based on created date
 * @author Sudhakar
 */
public class NoteComparator implements Comparator<Note> {

	@Override
	public int compare(Note note1, Note note2) {
		if(!(note1 instanceof Note  && note2 instanceof Note))
			throw new IllegalArgumentException("Can't compare non-Note objects");
		return note2.getCreatedDate().compareTo(note1.getCreatedDate());
	}
}