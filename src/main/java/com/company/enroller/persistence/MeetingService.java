package com.company.enroller.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;

@Component("meetingService")
public class MeetingService {

	DatabaseConnector connector;
	Session session;

	public MeetingService() {
		connector = DatabaseConnector.getInstance();
		session = connector.getSession();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = session.createQuery(hql);
		List<Meeting> meetings = query.list();
		Collections.sort(meetings);
		return meetings;
	}

	public Meeting findByID(long id) {
		return (Meeting) session.get(Meeting.class, id);
	}

	public void registerMeeting(Meeting meeting) {

		Transaction transaction = this.session.beginTransaction();

		session.save(meeting);
		transaction.commit();
	}

	public void deleteMeeting(Meeting meeting) {

		Transaction transaction = this.session.beginTransaction();

		session.delete(meeting);
		transaction.commit();
	}

	public void updateMeeting(long ID, Meeting updated_meeting) {
		Meeting meeting = this.findByID(ID);
		meeting.update(updated_meeting);

		Transaction transaction = this.session.beginTransaction();
		session.update(meeting);
		transaction.commit();
	}

	public void updateMeeting(Meeting meeting, Meeting updated_meeting) {
		meeting.update(updated_meeting);

		Transaction transaction = this.session.beginTransaction();
		session.update(meeting);
		transaction.commit();
	}

	public void addParticipant(Meeting foundMeeting, Participant new_participant) {
		foundMeeting.addParticipant(new_participant);
		this.updateMeeting(foundMeeting.getId(), foundMeeting);
	}

	public void removeParticipant(Meeting foundMeeting, Participant participant) {
		foundMeeting.removeParticipant(participant);
		this.updateMeeting(foundMeeting.getId(), foundMeeting);
	}

	public Collection<Participant> getMeetingParticipants(long meetingID) {
		Meeting meeting = this.findByID(meetingID);
		Collection<Participant> participants = meeting.getParticipants();
		return participants;
	}

	public Collection<Meeting> searchMeetings(String substring) {
		Collection<Meeting> meetings = this.getAll();
		Collection<Meeting> selectedMeetings = new ArrayList();
		for (Meeting meeting : meetings) {
			if (meeting.getTitle().contains(substring) || meeting.getDescription().contains(substring)) {
				selectedMeetings.add(meeting);
			}
		}

		if (selectedMeetings.size() == 0) {
			return null;
		}

		return selectedMeetings;
	}

	public Collection<Meeting> searchMeetingsByParticipant(String participantID) {
		Collection<Meeting> meetings = new ArrayList();

		for (Meeting meeting : this.getAll()) {
			for (Participant participant : meeting.getParticipants()) {
				if (participant.getLogin().equals(participantID)) {
					meetings.add(meeting);
					break;
				}
			}
		}
		return meetings;
	}
}
