<#-- @ftlvariable name="" type="com.inspirationlogical.receipt.reserver.view.GetReservationsView" -->
<html>
    <head>
        <script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
        <script type="text/javascript" src="/assets/js/jquery.ajax-cross-origin.min.js"></script>
    </head>
    <body>
    <#list reservations as reservation>
        <p>${reservation.name}: table ${reservation.tableNumber}</p>
    </#list>
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
    <script type="text/javascript">
    $("#addReservation").submit(function(e){
        return false;
    });
    function submitForm(){
        var $form = $("#addReservation");
        var data = getFormData($form);
        $.ajax({
            crossDomain: true,
            crossOrigin: true,
            type: "POST",
            url: "http://localhost:9000/reservation/",
            data: JSON.stringify(data),
            contentType: "application/json",
            dataType: 'json',
            xhrFields: {
                withCredentials: true
            },
            success: function() {
                location.reload();
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
    </script>
    </body>
</html>