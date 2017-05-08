$(document).ready(function() {
    $("#addReservation").submit(function(e){
        return false;
    });

    var apiUrl = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '')+'/reservation/api';

    $.ajaxSetup({
      url: apiUrl,
      dataType: "json"
    });

    var calendar = $('#calendar');

    calendar.fullCalendar({
        header: {
            left:   'title',
            center: '',
            right:  'today,month,agendaDay,agendaWeek prev,next'
        },
        eventRender: function (event, element) {
            element.attr('href', 'javascript:void(0);');
            element.click(function() {
                $("#eventStartTime").html(moment(event.start).format("HH:mm"));
                $("#eventEndTime").html(moment(event.end).format("HH:mm"));
                $("#eventPhone").html(event.phone);
                $("#eventGuests").html(event.guests);
                $("#eventInfo").html(event.description);
                $("#eventContent").dialog({ modal: true, title: event.title});
            });
        }
    });

    var localDate = $("#date").val();

    if (localDate == "") {
        calendar.fullCalendar('changeView', 'month');
    } else {
        calendar.fullCalendar('changeView', 'agendaDay', localDate);
    }

    getEvents(calendar);
});

function getEvents(calendar) {
    $.ajax({
        type: "GET",
        success: function (data) {
            $.each( data, function(index, reservation) {
                var event = new Object();
                event.id = index;
                event.title = "[Table " + reservation.tableNumber + "] " + reservation.name;
                event.start = reservation.date + " " + reservation.startTime;
                event.end = reservation.date + " " + reservation.endTime;
                event.phone = reservation.phoneNumber;
                event.guests = reservation.guestCount;
                event.description = reservation.note;
                calendar.fullCalendar('renderEvent', event);
            });
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