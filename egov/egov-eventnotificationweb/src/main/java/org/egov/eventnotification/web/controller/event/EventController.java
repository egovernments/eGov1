package org.egov.eventnotification.web.controller.event;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.egov.eventnotification.constants.EventnotificationConstant;
import org.egov.eventnotification.entity.Event;
import org.egov.eventnotification.service.EventService;
import org.egov.eventnotification.utils.EventnotificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * This is the EventController class. Which is basically used to create, view and update the event.
 * @author somvit
 *
 */
@Controller
@RequestMapping(value = EventnotificationConstant.API_EVENT)
public class EventController {

    private static final Logger LOGGER = Logger.getLogger(EventController.class);
    @Autowired
    private EventService eventService;

    @Autowired
    private MessageSource messageSource;

    /**
     * This method is used for view all event and view event by id.
     * @param model
     * @param id
     * @return tiles view
     */
    @RequestMapping(value = { EventnotificationConstant.API_VIEW }, method = RequestMethod.GET)
    public String view(final Model model) {
        model.addAttribute(EventnotificationConstant.EVENT_LIST, eventService.findAll(new Date()));
        model.addAttribute(EventnotificationConstant.MODE, EventnotificationConstant.MODE_VIEW);
        return EventnotificationConstant.VIEW_EVENTVIEW;
    }

    /**
     * This method is used for view all event and view event by id.
     * @param model
     * @param id
     * @return tiles view
     */
    @RequestMapping(value = { EventnotificationConstant.API_VIEW_ID }, method = RequestMethod.GET)
    public String viewById(final Model model, @PathVariable(EventnotificationConstant.EVENT_ID) Long id) {
        DateFormat formatter = new SimpleDateFormat(EventnotificationConstant.DDMMYYYY);
        Event event = eventService.findById(id);

        try {
            Date sd = new Date(event.getStartDate());
            event.setStartDt(formatter.parse(formatter.format(sd)));
            Date ed = new Date(event.getEndDate());
            event.setEndDt(formatter.parse(formatter.format(ed)));
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }

        model.addAttribute(EventnotificationConstant.EVENT, event);
        model.addAttribute(EventnotificationConstant.MODE, EventnotificationConstant.MODE_VIEW);
        return EventnotificationConstant.VIEW_EVENTVIEWRESULT;
    }

    /**
     * This method is used for show the create event page. It will take fetch all the hours, minutes and event type.
     * @param event
     * @param model
     * @return tiles view
     */
    @RequestMapping(value = EventnotificationConstant.API_CREATE, method = RequestMethod.GET)
    public String newEvent(@ModelAttribute Event event, Model model) {
        model.addAttribute(EventnotificationConstant.EVENT, event);
        model.addAttribute(EventnotificationConstant.HOUR_LIST, EventnotificationUtil.getAllHour());
        model.addAttribute(EventnotificationConstant.MINUTE_LIST, EventnotificationUtil.getAllMinute());
        List eventList = new ArrayList<>(Arrays.asList(EventnotificationConstant.EVENT_TYPE.values()));
        model.addAttribute(EventnotificationConstant.EVENT_LIST, eventList);
        model.addAttribute(EventnotificationConstant.MODE, EventnotificationConstant.MODE_CREATE);
        return EventnotificationConstant.VIEW_EVENTCREATE;
    }

    /**
     * This method is used for create event page.
     * @param event
     * @param files
     * @param model
     * @param redirectAttrs
     * @param request
     * @param errors
     * @return tiles view
     * @throws IOException
     * @throws ParseException
     */
    @RequestMapping(value = EventnotificationConstant.API_CREATE, method = RequestMethod.POST)
    public String create(@ModelAttribute(EventnotificationConstant.EVENT) Event event,
            @RequestParam(EventnotificationConstant.FILE) MultipartFile[] files, Model model,
            RedirectAttributes redirectAttrs, HttpServletRequest request, BindingResult errors)
            throws IOException, ParseException {

        if (errors.hasErrors()) {
            model.addAttribute(EventnotificationConstant.MODE, EventnotificationConstant.MODE_CREATE);
            model.addAttribute(EventnotificationConstant.HOUR_LIST, EventnotificationUtil.getAllHour());
            model.addAttribute(EventnotificationConstant.MINUTE_LIST, EventnotificationUtil.getAllMinute());
            List eventList = new ArrayList<>(Arrays.asList(EventnotificationConstant.EVENT_TYPE.values()));
            model.addAttribute(EventnotificationConstant.EVENT_LIST, eventList);
            return EventnotificationConstant.VIEW_EVENTCREATE;
        }
        event.setStartDate(event.getStartDt().getTime());
        event.setEndDate(event.getEndDt().getTime());
        event.setStartTime(event.getStartHH() + ":" + event.getStartMM());
        event.setEndTime(event.getEndHH() + ":" + event.getEndMM());

        eventService.persist(event, files);

        redirectAttrs.addFlashAttribute(EventnotificationConstant.EVENT, event);
        model.addAttribute(EventnotificationConstant.MESSAGE,
                messageSource.getMessage(EventnotificationConstant.MSG_EVENT_CREATE_SUCCESS, null, Locale.ENGLISH));
        model.addAttribute(EventnotificationConstant.MODE, EventnotificationConstant.MODE_VIEW);
        return EventnotificationConstant.VIEW_EVENTSUCCESS;
    }

    /**
     * This method is used for show the event update page based on the event id.
     * @param event
     * @param model
     * @param id
     * @return tiles view
     */
    @RequestMapping(value = EventnotificationConstant.API_UPDATE_ID, method = RequestMethod.GET)
    public String viewUpdate(@ModelAttribute Event event, Model model,
            @PathVariable(EventnotificationConstant.EVENT_ID) Long id) {
        Event eventObj = eventService.findById(id);
        DateFormat formatter = new SimpleDateFormat(EventnotificationConstant.DDMMYYYY);
        try {
            Date sd = new Date(eventObj.getStartDate());
            eventObj.setStartDt(formatter.parse(formatter.format(sd)));
            Date ed = new Date(eventObj.getEndDate());
            eventObj.setEndDt(formatter.parse(formatter.format(ed)));
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        String[] st = eventObj.getStartTime().split(":");
        eventObj.setStartHH(st[0]);
        eventObj.setStartMM(st[1]);
        String[] et = eventObj.getEndTime().split(":");
        eventObj.setEndHH(et[0]);
        eventObj.setEndMM(et[1]);
        model.addAttribute(EventnotificationConstant.EVENT, eventObj);
        model.addAttribute(EventnotificationConstant.HOUR_LIST, EventnotificationUtil.getAllHour());
        model.addAttribute(EventnotificationConstant.MINUTE_LIST, EventnotificationUtil.getAllMinute());
        List eventList = new ArrayList<>(Arrays.asList(EventnotificationConstant.EVENT_TYPE.values()));
        model.addAttribute(EventnotificationConstant.EVENT_LIST, eventList);
        model.addAttribute(EventnotificationConstant.MODE, EventnotificationConstant.MODE_UPDATE);
        return EventnotificationConstant.VIEW_EVENTUPDATE;
    }

    /**
     * This method is used for update the event.
     * @param event
     * @param files
     * @param model
     * @param redirectAttrs
     * @param request
     * @param errors
     * @param id
     * @return tiles view
     * @throws IOException
     * @throws ParseException
     */
    @RequestMapping(value = EventnotificationConstant.API_UPDATE_ID, method = RequestMethod.POST)
    public String update(@ModelAttribute(EventnotificationConstant.EVENT) Event event,
            @RequestParam(EventnotificationConstant.FILE) MultipartFile[] files, Model model,
            RedirectAttributes redirectAttrs, HttpServletRequest request, BindingResult errors,
            @PathVariable(EventnotificationConstant.EVENT_ID) Long id)
            throws IOException, ParseException {
        event.setId(id);
        event.setStartDate(event.getStartDt().getTime());
        event.setEndDate(event.getEndDt().getTime());
        event.setStartTime(event.getStartHH() + ":" + event.getStartMM());
        event.setEndTime(event.getEndHH() + ":" + event.getEndMM());

        eventService.update(event, files);

        redirectAttrs.addFlashAttribute(EventnotificationConstant.EVENT, event);
        model.addAttribute(EventnotificationConstant.MESSAGE,
                messageSource.getMessage(EventnotificationConstant.MSG_EVENT_UPDATE_SUCCESS, null, Locale.ENGLISH));
        model.addAttribute(EventnotificationConstant.MODE, EventnotificationConstant.MODE_VIEW);
        return EventnotificationConstant.VIEW_EVENTUPDATESUCCESS;
    }

}
