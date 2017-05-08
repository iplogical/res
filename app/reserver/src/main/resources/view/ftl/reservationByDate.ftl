<#-- @ftlvariable name="" type="com.inspirationlogical.receipt.reserver.view.GetReservationsByDateView" -->
<#import "reservationBase.ftl" as reservationBase>
<#import "reservationForm.ftl" as reservationForm>
<#import "reservationView.ftl" as reservationView>
<@reservationBase.base>
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
    <@reservationForm.addReservation />
    <@reservationView.getCalendar />
</@reservationBase.base>