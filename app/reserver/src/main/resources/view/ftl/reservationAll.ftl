<#-- @ftlvariable name="" type="com.inspirationlogical.receipt.reserver.view.GetReservationsView" -->
<#import "reservationBase.ftl" as reservationBase>
<#import "reservationForm.ftl" as reservationForm>
<#import "reservationView.ftl" as reservationView>
<@reservationBase.base>
    <@reservationForm.addReservation />
    <@reservationForm.editReservation />
    <@reservationForm.showReservation />
    <@reservationView.getCalendar />
</@reservationBase.base>