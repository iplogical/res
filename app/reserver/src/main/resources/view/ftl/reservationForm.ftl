<#macro addReservation>
    <div id="addReservationForm" title="Add reservation" style="display:none;">
        <form action="" method="post" id="addReservation">
            Name:   <input name="name" id="addName"/><br/>
            Note:   <input name="note" id="addNote"/><br/>
            Phone:  <input name="phoneNumber" id="addPhoneNumber"/><br/>
            Table:  <input name="tableNumber" id="addTableNumber"/><br/>
            Guests: <input name="guestCount" id="addGuestCount"/><br/>
            Date:   <input name="date" id="addDate" placeholder="yyyy-MM-dd" value="${localDate!}"/><br/>
            Start:  <input name="startTime" id="addStartTime" placeholder="hh:mm"/><br/>
            End:    <input name="endTime" id="addEndTime" placeholder="hh:mm"/><br/>
            <input type="submit" value="Add" onclick="submitForm()"/>
        </form>
    </div>
</#macro>