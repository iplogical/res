<#macro addReservation>
    <fieldset>
        <legend>Add Reservation</legend>
        <form action="" method="post" id="addReservation">
            Name:   <input name="name" id="name"/><br/>
            Note:   <input name="note" id="note"/><br/>
            Phone:  <input name="phoneNumber" id="phoneNumber"/><br/>
            Table:  <input name="tableNumber" id="tableNumber"/><br/>
            Guests: <input name="guestCount" id="guestCount"/><br/>
            Date:   <input name="date" id="date" placeholder="yyyy-MM-dd" value="${localDate!}"/><br/>
            Start:  <input name="startTime" id="startTime" placeholder="hh:mm"/><br/>
            End:    <input name="endTime" id="endTime" placeholder="hh:mm"/><br/>
            <input type="submit" value="Add" onclick="submitForm()"/>
        </form>
    </fieldset>
</#macro>