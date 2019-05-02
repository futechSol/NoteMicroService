package com.bridgelabz.noteMicroService.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AccessLevel;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Note implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(updatable = false)
	@Setter(AccessLevel.PRIVATE)
	private long id;
	private String title;
	@Lob
	private String description;
	private String color;
	private boolean isPinned;
	private boolean isArchived;
	private boolean isTrashed;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;
	private String reminder;
	private String repeatReminder;
    private long userId;
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Note) {
			Note note = (Note) obj;
			if (this.title.equals(note.title) && this.description.equals(note.description))
				return true;
			else
				return false;
		}
		throw new IllegalArgumentException("Can't compare non-Note objects");
	}
}
