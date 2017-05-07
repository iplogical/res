<#-- @ftlvariable name="" type="com.inspirationlogical.receipt.reserver.view.GetReservationsView" -->
<html>
    <head>
        <script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
        <script type="text/javascript" src="/assets/js/reservation.js"></script>
        <link rel="stylesheet" type="text/css" href="/assets/css/reservation.css">

        <script type="text/javascript" src="https://momentjs.com/downloads/moment.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.18.1/locale/hu.js"></script>

        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.4.0/fullcalendar.min.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.4.0/locale/hu.js"></script>
        <link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/fullcalendar/3.4.0/fullcalendar.min.css">
    </head>
    <body>
    <table>
        <tr>
            <th>Name</th>
            <th>Note</th>
            <th>Phone</th>
            <th>Table</th>
            <th>Guests</th>
            <th>Date</th>
            <th>Start</th>
            <th>End</th>
        </tr>
        <#list reservations as reservation>
            <tr>
                <td>${reservation.name!}</td>
                <td>${reservation.note!}</td>
                <td>${reservation.phoneNumber!}</td>
                <td>${reservation.tableNumber!}</td>
                <td>${reservation.guestCount!}</td>
                <td>${reservation.date!}</td>
                <td>${reservation.startTime!}</td>
                <td>${reservation.endTime!}</td>
            </tr>
        </#list>
    </table>
    <fieldset>
        <legend>Add Reservation</legend>
        <form action="" method="post" id="addReservation">
            Name:   <input name="name" id="name"/><br/>
            Note:   <input name="note" id="note"/><br/>
            Phone:  <input name="phoneNumber" id="phoneNumber"/><br/>
            Table:  <input name="tableNumber" id="tableNumber"/><br/>
            Guests: <input name="guestCount" id="guestCount"/><br/>
            Date:   <input name="date" id="date" placeholder="yyyy-MM-dd"/><br/>
            Start:  <input name="startTime" id="startTime" placeholder="hh:mm:ss"/><br/>
            End:    <input name="endTime" id="endTime" placeholder="hh:mm:ss"/><br/>
            <input type="submit" value="Add" onclick="submitForm()"/>
        </form>
    </fieldset>
    <div id='calendar'></div>
    </body>
</html>