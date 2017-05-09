<#macro addReservation>
    <div id="addReservationForm" title="Add reservation" style="display:none;">
        <form action="" method="post" id="addReservation">
            <label>Name</label><input type="text" name="name" id="addName"/>
            <label>Date</label><input type="date" name="date" id="addDate" placeholder="yyyy-MM-dd" value="${localDate!}"/>
            <label>Start</label><input type="time" name="startTime" id="addStartTime" placeholder="hh:mm"/>
            <label>End</label><input type="time" name="endTime" id="addEndTime" placeholder="hh:mm"/>
            <label>Table</label><input type="number" name="tableNumber" id="addTableNumber"/>
            <label>Guests</label><input type="number" name="guestCount" id="addGuestCount"/>
            <label>Phone</label><input type="tel" name="phoneNumber" id="addPhoneNumber"/>
            <label>Note</label><textarea name="note" id="addNote"></textarea>
        </form>
    </div>
</#macro>

<#macro editReservation>
    <div id="editReservationForm" title="Edit reservation" style="display:none;">
        <form action="" method="post" id="editReservation">
            <label>Name</label><input type="text" name="name" id="editName"/>
            <label>Date</label><input type="date" name="date" id="editDate" placeholder="yyyy-MM-dd"/>
            <label>Start</label><input type="time" name="startTime" id="editStartTime" placeholder="hh:mm"/>
            <label>End</label><input type="time" name="endTime" id="editEndTime" placeholder="hh:mm"/>
            <label>Table</label><input type="number" name="tableNumber" id="editTableNumber"/>
            <label>Guests</label><input type="number" name="guestCount" id="editGuestCount"/>
            <label>Phone</label><input type="tel" name="phoneNumber" id="editPhoneNumber"/>
            <label>Note</label><textarea name="note" id="editNote"></textarea>
            <input type="hidden" name="reservationId" id="editId"/>
        </form>
    </div>
</#macro>

<#macro showReservation>
    <div id="showReservationForm" title="Reservation" style="display:none;">
        <label>Date
            <span id="showDate"></span>
        </label>
        <label>Start
            <span id="showStartTime"></span>
        </label>
        <label>End
            <span id="showEndTime"></span>
        </label>
        <label>Guests
            <span id="showGuests"></span>
        </label>
        <label>Phone
            <span id="showPhone"></span>
        </label>
        <label>Note
            <span id="showNote"></span>
        </label>
    </div>
</#macro>