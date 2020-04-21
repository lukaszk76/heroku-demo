package com.company.enroller.persistence;

import java.util.Collection;

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
		return query.list();
	}
	
	public Meeting findByID(long id) {
		return (Meeting)session.get(Meeting.class, id );
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
	
}
