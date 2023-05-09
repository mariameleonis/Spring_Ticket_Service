package com.example.service.facade;

import com.example.entity.Event;
import com.example.entity.Ticket;
import com.example.entity.User;
import com.example.entity.UserAccount;
import com.example.service.exception.BusinessException;
import com.example.service.exception.EventNotFoundException;
import com.example.service.exception.TicketNotFoundException;
import com.example.service.exception.UserNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
   * Groups together all operations related to tickets booking.
   * Created by maksym_govorischev.
   */
  public interface BookingFacade {

    /**
     * Gets event by its id.
     * @return Event.
     */
    Event getEventById(long eventId) throws EventNotFoundException;

    /**
     * Get list of events by matching title. Title is matched using 'contains' approach.
     * In case nothing was found, empty list is returned.
     * @param title Event title or it's part.
     * @param pageSize Pagination param. Number of events to return on a page.
     * @param pageNum Pagination param. Number of the page to return. Starts from 1.
     * @return List of events.
     */
    List<Event> getEventsByTitle(String title, int pageSize, int pageNum);

    /**
     * Get list of events for specified day.
     * In case nothing was found, empty list is returned.
     * @param day Date object from which day information is extracted.
     * @param pageSize Pagination param. Number of events to return on a page.
     * @param pageNum Pagination param. Number of the page to return. Starts from 1.
     * @return List of events.
     */
    List<Event> getEventsForDay(LocalDate day, int pageSize, int pageNum);

    /**
     * Creates new event. Event id should be auto-generated.
     * @param event Event data.
     * @return Created Event object.
     */
    Event createEvent(Event event);

    /**
     * Updates event using given data.
     * @param event Event data for update. Should have id set.
     * @return Updated Event object.
     */
    Event updateEvent(Event event) throws BusinessException;

    /**
     * Deletes event by its id.
     * @param eventId Event id.
     */
    void deleteEvent(long eventId);

    /**
     * Gets user by its id.
     * @return User.
     */
    User getUserById(long userId) throws UserNotFoundException;

    /**
     * Gets user by its email. Email is strictly matched.
     * @return User.
     */
    User getUserByEmail(String email) throws UserNotFoundException;

    /**
     * Get list of users by matching name. Name is matched using 'contains' approach.
     * In case nothing was found, empty list is returned.
     * @param name Users name or it's part.
     * @param pageSize Pagination param. Number of users to return on a page.
     * @param pageNum Pagination param. Number of the page to return. Starts from 1.
     * @return List of users.
     */
    List<User> getUsersByName(String name, int pageSize, int pageNum);

    /**
     * Creates new user. User id should be auto-generated.
     * @param user User data.
     * @return Created User object.
     */
    User createUser(User user) throws BusinessException;

    /**
     * Updates user using given data.
     * @param user User data for update. Should have id set.
     * @return Updated User object.
     */
    User updateUser(User user) throws UserNotFoundException;

    /**
     * Deletes user by its id.
     * @param userId User id.
     */
    void deleteUser(long userId);

    /**
     * Book ticket for a specified event on behalf of specified user.
     * @param userId User Id.
     * @param eventId Event Id.
     * @param place Place number.
     * @return Booked ticket object.
     * @throws java.lang.IllegalStateException if this place has already been booked.
     */
    Ticket bookTicket(long userId, long eventId, int place) throws BusinessException;

    /**
     * Get all booked tickets for specified user. Tickets should be sorted by event date in descending order.
     * @param user User
     * @param pageSize Pagination param. Number of tickets to return on a page.
     * @param pageNum Pagination param. Number of the page to return. Starts from 1.
     * @return List of Ticket objects.
     */
    List<Ticket> getBookedTickets(User user, int pageSize, int pageNum);

    /**
     * Get all booked tickets for specified event. Tickets should be sorted in by user email in ascending order.
     * @param event Event
     * @param pageSize Pagination param. Number of tickets to return on a page.
     * @param pageNum Pagination param. Number of the page to return. Starts from 1.
     * @return List of Ticket objects.
     */
    List<Ticket> getBookedTickets(Event event, int pageSize, int pageNum);

    /**
     * Cancel ticket with a specified id.
     * @param ticketId Ticket id.
     */
    void cancelTicket(Long ticketId) throws TicketNotFoundException, UserNotFoundException;

    void refillUserAccountByUserId(Long userId, BigDecimal amount) throws BusinessException;

    UserAccount findUserAccountByUserId(Long userId) throws BusinessException;
}

