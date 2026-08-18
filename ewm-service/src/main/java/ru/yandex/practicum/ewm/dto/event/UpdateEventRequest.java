package ru.yandex.practicum.ewm.dto.event;

 interface UpdateEventRequest {
     boolean hasAnnotation() ;
     boolean hasCategory() ;
     boolean hasDescription() ;
     boolean hasEventDate() ;
     boolean hasLocation() ;
     boolean hasPaid() ;
     boolean hasParticipantLimit() ;
     boolean hasRequestModeration() ;
     boolean hasTitle() ;
     boolean hasStateAction() ;}
