<#-- @ftlvariable name="" type="com.inspirationlogical.receipt.reserver.view.GetReservationsView" -->
<html>
    <body>
    <#list reservations as reservation>
        <p>${reservation.name}: table ${reservation.tableNumber}</p>
    </#list>
    </body>
</html>
