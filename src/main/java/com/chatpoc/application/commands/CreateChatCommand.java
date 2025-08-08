package com.chatpoc.application.commands;

public record CreateChatCommand(
    String chatName,
    String creatorName,
    String creatorEmail
) {}