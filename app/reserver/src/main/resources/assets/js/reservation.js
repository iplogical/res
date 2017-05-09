
var apiUrl = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '')+'/reservation/api';

$(document).ready(function() {
    $("#addReservation").submit(function(e){
        return false;
    });

    $.ajaxSetup({
      url: apiUrl,
      dataType: "json"
    });

    var calendar = $('#calendar');

    calendar.fullCalendar({
        customButtons: {
            addReservationButton: {
                text: 'Add',
                click: function() {
                    $("#addReservationForm").dialog({modal: true});
                }
            }
        },
        header: {
            left:   'title',
            center: 'addReservationButton',
            right:  'today,agendaDay,agendaWeek,month prev,next'
        },
        eventRender: function (event, element) {
            element.attr('href', 'javascript:void(0);');
            element.click(function() {
                $("#showStartTime").html(moment(event.start).format("HH:mm"));
                $("#showEndTime").html(moment(event.end).format("HH:mm"));
                $("#showPhone").html(event.phone);
                $("#showGuests").html(event.guests);
                $("#showNote").html(event.description);
                $("#showReservationForm").dialog({ modal: true, title: event.title});
            });
        },
        selectable: true,
        selectHelper: true,
        select: function(start, end, event) {
            calendar.fullCalendar('unselect');
            $("#addDate").val(moment(start).format("YYYY-MM-DD"));
            if (calendar.fullCalendar('getView').name == "month") {
                calendar.fullCalendar('changeView', 'agendaDay', $("#addDate").val(), true);
            } else {
                $("#addStartTime").val(moment(start).format("HH:mm"));
                $("#addEndTime").val(moment(end).format("HH:mm"));
                $("#addReservationForm").dialog({modal: true});
            }
        },
        editable: true,
        eventDrop: function(event, delta, revertFunc) {
            if (!confirm("Confirm start time change")) {
                revertFunc();
            } else {
                updateReservation(event);
            }
        },
        eventResize: function(event, delta, revertFunc) {
            if (!confirm("Confirm end time change")) {
                revertFunc();
            } else {
                updateReservation(event);
            }
        }
    });

    var localDate = $("#addDate").val();

    if (localDate == "") {
        calendar.fullCalendar('changeView', 'month');
    } else {
        calendar.fullCalendar('changeView', 'agendaDay', localDate, true);
    }

    getEvents(calendar);
});

function getEvents(calendar) {
    $.ajax({
        type: "GET",
        success: function (data) {
            $.each( data, function(index, reservation) {
                var event = new Object();
                event.id = reservation.reservationId;
                event.table = reservation.tableNumber;
                event.name = reservation.name;
                event.title = "[" + reservation.tableNumber + "] " + reservation.name;
                event.start = reservation.date + " " + reservation.startTime;
                event.end = reservation.date + " " + reservation.endTime;
                event.phone = reservation.phoneNumber;
                event.guests = reservation.guestCount;
                event.description = reservation.note;
                calendar.fullCalendar('renderEvent', event, true);
            });
        }
    });
}

function updateReservation(event) {
    var reservation = new Object();
    reservation.name = event.name;
    reservation.note = event.description;
    reservation.phoneNumber = event.phone;
    reservation.tableNumber = event.table;
    reservation.guestCount = event.guests;
    reservation.date = moment(event.start).format("YYYY-MM-DD");
    reservation.startTime = moment(event.start).format("HH:mm");
    reservation.endTime = moment(event.end).format("HH:mm");

    $.ajax({
        type: "PUT",
        url: apiUrl + "/" + event.id,
        data: JSON.stringify(reservation),
        contentType: "application/json",
        success: function() {
//            location.reload();
        },
        error: function(error){
            console.log(error);
            alert(error.responseText);
        }
    });
}

function submitForm(e){
    var $form = $("#addReservation");
    var data = getFormData($form);
    $.ajax({
        type: "POST",
        data: JSON.stringify(data),
        contentType: "application/json",
        success: function() {
            location.reload();
        },
        error: function(error){
            console.log(error);
            alert(error.responseText);
        }
    });
}

function getFormData($form){
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};
    $.map(unindexed_array, function(n, i){
        indexed_array[n['name']] = n['value'];
    });
    return indexed_array;
}