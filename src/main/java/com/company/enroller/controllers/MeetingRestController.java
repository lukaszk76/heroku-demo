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
			return new ResponseEntity("Unable to create. A meeting with ID " + meeting.getId() + " already exist.", HttpStatus.CONFLICT);
		} else {
			meetingService.registerMeeting(meeting);	
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
}
