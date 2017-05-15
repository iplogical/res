
var url = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '');
var apiUrl = url+'/reservation/api';
var formWidth = "400px";

$(document).ready(function() {

    $("#addReservation").submit(function(e){
        return false;
    });

    $("#editReservation").submit(function(e){
        return false;
    });

    $.ajaxSetup({
      url: apiUrl,
      dataType: "json"
    });

    var calendar = $('#calendar');

    calendar.fullCalendar({
        theme: true,
        height: $(window).height() - 20,
        longPressDelay: 250,
        customButtons: {
            addReservationButton: {
                text: 'New reservation',
                click: function() {
                    showAddReservationForm(calendar);
                }
            },
            logoutButton: {
                text: 'Logout',
                click: function() {
                    logout(url+'/logout');
                }
            }
        },
        header: {
            left:   'title',
            center: '',
            right:  'addReservationButton today,agendaDay,agendaWeek,month prev,next logoutButton'
        },
        windowResize: function(view) {
            height: $(window).height() - 20
        },
        eventRender: function (event, element) {
            element.attr('href', 'javascript:void(0);');
            element.click(function() {
                updateShowReservationForm(event);
                $("#showReservationForm").dialog({
                    modal: true,
                    width: formWidth,
                    title: event.title,
                    buttons: {
                        Delete: function() {
                            if (confirm("Confirm delete")) {
                                deleteReservation(event, calendar);
                                $( this ).dialog( "close" );
                            }
                        },
                        Edit: function() {
                            $("#editId").val(event.id);
                            $("#editName").val(event.name);
                            $("#editNote").val(event.description);
                            $("#editPhoneNumber").val(event.phone);
                            $("#editTableNumber").val(event.table);
                            $("#editGuestCount").val(event.guests);
                            $("#editDate").val(moment(event.start).format("YYYY-MM-DD"));
                            $("#editStartTime").val(moment(event.start).format("HH:mm"));
                            $("#editEndTime").val(moment(event.end).format("HH:mm"));
                            $("#editReservationForm").dialog({
                                modal: true,
                                width: formWidth,
                                buttons: {
                                    Save: function() {
                                        var $form = $("#editReservation");
                                        var reservation = getFormData($form);
                                        updateReservation(reservation, event, calendar);
                                        $( this ).dialog( "close" );
                                    },
                                    Cancel: function() {
                                        $( this ).dialog( "close" );
                                    }
                                }
                            });
                        },
                        Close: function() {
                            $( this ).dialog( "close" );
                        }
                    }
                });
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
                showAddReservationForm(calendar);
            }
        },
        editable: true,
        eventDrop: function(event, delta, revertFunc) {
            if (!confirm("Confirm start time change")) {
                revertFunc();
            } else {
                updateReservationTime(event, calendar, revertFunc);
            }
        },
        eventResize: function(event, delta, revertFunc) {
            if (!confirm("Confirm end time change")) {
                revertFunc();
            } else {
                updateReservationTime(event, calendar, revertFunc);
            }
        }
    });

    var localDate = $("#addDate").val();

    if (localDate == "") {
        calendar.fullCalendar('changeView', 'month');
    } else {
        calendar.fullCalendar('changeView', 'agendaDay', localDate, true);
    }

    getReservations(calendar);
});

function logout(to_url) {
    var out = window.location.href.replace(/:\/\//, '://log:out@');

    jQuery.get(out).error(function() {
        window.location = to_url;
    });
}

function updateShowReservationForm(event) {
    $("#showDate").html(moment(event.start).format("YYYY-MM-DD"));
    $("#showStartTime").html(moment(event.start).format("HH:mm"));
    $("#showEndTime").html(moment(event.end).format("HH:mm"));
    $("#showPhone").html(event.phone);
    $("#showGuests").html(event.guests);
    $("#showNote").html(event.description);
}

function showAddReservationForm(calendar) {
    $("#addReservationForm").dialog({
        modal: true,
        width: formWidth,
        buttons: {
            Save: function() {
                var $form = $("#addReservation");
                var reservation = getFormData($form);
                addReservation(reservation, calendar);
                $( this ).dialog( "close" );
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
}

function reservationToEvent(reservation, event) {
    event.id = reservation.reservationId;
    event.table = reservation.tableNumber;
    event.name = reservation.name;
    event.title = "[" + reservation.tableNumber + "] " + reservation.name;
    event.start = reservation.date + " " + reservation.startTime;
    event.end = reservation.date + " " + reservation.endTime;
    event.phone = reservation.phoneNumber;
    event.guests = reservation.guestCount;
    event.description = reservation.note;
    return event;
}

function eventToReservation(event) {
    var reservation = new Object();
    reservation.reservationId = event.id;
    reservation.name = event.name;
    reservation.note = event.description;
    reservation.phoneNumber = event.phone;
    reservation.tableNumber = event.table;
    reservation.guestCount = event.guests;
    reservation.date = moment(event.start).format("YYYY-MM-DD");
    reservation.startTime = moment(event.start).format("HH:mm");
    reservation.endTime = moment(event.end).format("HH:mm");
    return reservation;
}

function getReservations(calendar) {
    $.ajax({
        type: "GET",
        success: function (data) {
            $.each( data, function(index, reservation) {
                var event = reservationToEvent(reservation, new Object());
                calendar.fullCalendar('renderEvent', event, true);
            });
        }
    });
}

function addReservation(reservation, calendar){
    var event = reservationToEvent(reservation, new Object());
    $.ajax({
        type: "POST",
        data: JSON.stringify(reservation),
        contentType: "application/json",
        success: function(id) {
            event.id = id;
            calendar.fullCalendar('renderEvent', event, true);
        },
        error: function(error){
            console.log(error);
            alert(error.responseText);
        }
    });
}

function updateReservation(reservation, event, calendar) {
    $.ajax({
        type: "PUT",
        url: apiUrl + "/" + event.id,
        data: JSON.stringify(reservation),
        contentType: "application/json",
        success: function() {
            reservationToEvent(reservation, event);
            calendar.fullCalendar('updateEvent', event);
            updateShowReservationForm(event);
        },
        error: function(error){
            console.log(error);
            alert(error.responseText);
        }
    });
}

function updateReservationTime(event, calendar, revertFunc) {
    var reservation = eventToReservation(event);
    $.ajax({
        type: "PUT",
        url: apiUrl + "/" + event.id,
        data: JSON.stringify(reservation),
        contentType: "application/json",
        success: function() {
        },
        error: function(error){
            revertFunc();
            console.log(error);
            alert(error.responseText);
        }
    });
}

function deleteReservation(event, calendar) {
    $.ajax({
        type: "DELETE",
        url: apiUrl + "/" + event.id,
        success: function() {
            calendar.fullCalendar('removeEvents', event.id);
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