package com.company.enroller.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

	@Autowired
	MeetingService meetingService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetings() {
		Collection<Meeting> meetings = meetingService.getAll();
		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/SEARCH={substring}", method = RequestMethod.GET)
	public ResponseEntity<?> searchMeetings(@PathVariable("substring") String substring) {
	    Collection<Meeting> meetings = meetingService.searchMeetings(substring);
		if (meetings == null) { 
		return new ResponseEntity(HttpStatus.NOT_FOUND);
		} 
	
		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK); 
	}
	
	@RequestMapping(value = "/PARTICIPANT={participantID}", method = RequestMethod.GET)
	public ResponseEntity<?> searchMeetingsByParticipant(@PathVariable("participantID") String participantID) {
	    Collection<Meeting> meetings = meetingService.searchMeetingsByParticipant(participantID);
		if (meetings == null) { 
		return new ResponseEntity(HttpStatus.NOT_FOUND);
		} 
	
		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK); 
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)

	public ResponseEntity<?> getMeeting(@PathVariable("id") long meetingID) {
	    Meeting meeting = meetingService.findByID(meetingID);
		if (meeting == null) { 
		return new ResponseEntity(HttpStatus.NOT_FOUND);
		} 
	
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK); 
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST) 
	public ResponseEntity<?> registerMeeting(@RequestBody Meeting meeting){
		
		Meeting foundMeeting = meetingService.findByID(meeting.getId());
		if (foundMeeting != null) {
			return new ResponseEntity("Unable to create. A meeting with ID " + meeting.getId() + " already exists.", HttpStatus.CONFLICT);
		} else {
			meetingService.registerMeeting(meeting);	
		} 
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT) 
	public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting meeting){
		
		Meeting foundMeeting = meetingService.findByID(id);
		if (foundMeeting == null) {
			meetingService.registerMeeting(meeting);
			return new ResponseEntity("The meeting with ID " + meeting.getId() + " did not exist. The meeting was created instead of updated.", HttpStatus.CONFLICT);
		} else {
			meetingService.updateMeeting(id, meeting);	
		} 
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)

	public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
		
		Meeting foundMeeting = meetingService.findByID(id);
		if (foundMeeting == null) {
			return new ResponseEntity("Unable to delete! A participant with login " + id + " does not exist.", HttpStatus.CONFLICT);
		} else {
			meetingService.deleteMeeting(foundMeeting);
		} 
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}/{login}", method = RequestMethod.POST)

	public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long id, @PathVariable("login") String login) {
		
		ParticipantService participantService = new ParticipantService();
		Participant participant = participantService.findByLogin(login);
		if (participant == null) {
			return new ResponseEntity("Add the participant " + login + " to the general list of participants first.", HttpStatus.CONFLICT);
		}
		
		
		Meeting foundMeeting = meetingService.findByID(id);
		if (foundMeeting == null) {
			return new ResponseEntity("Unable to add a participant. Meeting with " + id + " does not exist.", HttpStatus.CONFLICT);
		}
		
		for (Participant meetingParticipant: foundMeeting.getParticipants()) {
			if (meetingParticipant.getLogin().equals(login)){
				return new ResponseEntity("Unable to add a participant. Participant  " + login + " already added to the meeting.", HttpStatus.CONFLICT);
			}	
		} 
		
		meetingService.addParticipant(foundMeeting, participant);
		
		return new ResponseEntity(HttpStatus.OK);
	} 
	
	@RequestMapping(value = "/{id}/{login}", method = RequestMethod.DELETE)
	public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable("id") long id, @PathVariable("login") String login) {
		
		Meeting foundMeeting = meetingService.findByID(id);
		if (foundMeeting == null) {
			return new ResponseEntity("Unable to remove the participant. Meeting with " + id + " does not exist.", HttpStatus.CONFLICT);
		}
		
		Participant foundParticipant = null; 
		
		for (Participant meetingParticipant: foundMeeting.getParticipants()) {
			if (meetingParticipant.getLogin().equals(login)){
				foundParticipant = meetingParticipant;
				break;		
			}	
		} 
		
		if (foundParticipant == null) {
			return new ResponseEntity("Unable to remove the participant. Participant  " + login + " was not registered to the meeting.", HttpStatus.CONFLICT);
		}
		
		meetingService.removeParticipant(foundMeeting, foundParticipant);
		
		return new ResponseEntity(HttpStatus.OK);
	} 
	
	@RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long meetingID) {
	    
		Collection<Participant> participants = meetingService.getMeetingParticipants(meetingID);
		if (participants == null) { 
		return new ResponseEntity(HttpStatus.NOT_FOUND);
		} 
		
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK); 
	}
}
