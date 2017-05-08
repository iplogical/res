<#-- @ftlvariable name="" type="com.inspirationlogical.receipt.reserver.view.GetReservationsView" -->
<#import "reservationBase.ftl" as reservationBase>
<#import "reservationForm.ftl" as reservationForm>
<#import "reservationView.ftl" as reservationView>
<@reservationBase.base>
    <@reservationForm.addReservation />
    <@reservationView.getCalendar />
</@reservationBase.base>