<#macro addReservation>
    <div id="addReservationForm" title="Add reservation" style="display:none;">
        <form action="" method="post" id="addReservation">
            Name:   <input type="text" name="name" id="addName"/><br/>
            Note:   <input type="text" name="note" id="addNote"/><br/>
            Phone:  <input type="tel" name="phoneNumber" id="addPhoneNumber"/><br/>
            Table:  <input type="number" name="tableNumber" id="addTableNumber"/><br/>
            Guests: <input type="number" name="guestCount" id="addGuestCount"/><br/>
            Date:   <input type="date" name="date" id="addDate" placeholder="yyyy-MM-dd" value="${localDate!}"/><br/>
            Start:  <input type="time" name="startTime" id="addStartTime" placeholder="hh:mm"/><br/>
            End:    <input type="time" name="endTime" id="addEndTime" placeholder="hh:mm"/><br/>
        </form>
    </div>
</#macro>

<#macro editReservation>
    <div id="editReservationForm" title="Edit reservation" style="display:none;">
        <form action="" method="post" id="editReservation">
            Name:   <input type="text" name="name" id="editName"/><br/>
            Note:   <input type="text" name="note" id="editNote"/><br/>
            Phone:  <input type="tel" name="phoneNumber" id="editPhoneNumber"/><br/>
            Table:  <input type="number" name="tableNumber" id="editTableNumber"/><br/>
            Guests: <input type="number" name="guestCount" id="editGuestCount"/><br/>
            Date:   <input type="date" name="date" id="editDate" placeholder="yyyy-MM-dd"/><br/>
            Start:  <input type="time" name="startTime" id="editStartTime" placeholder="hh:mm"/><br/>
            End:    <input type="time" name="endTime" id="editEndTime" placeholder="hh:mm"/><br/>
            <input type="hidden" name="reservationId" id="editId"/>
        </form>
    </div>
</#macro>

<#macro showReservation>
    <div id="showReservationForm" title="Reservation" style="display:none;">
        Start: <span id="showStartTime"></span><br>
        End: <span id="showEndTime"></span><br>
        Phone: <span id="showPhone"></span><br>
        Guests: <span id="showGuests"></span><br>
        <p id="showNote"></p>
    </div>
</#macro>