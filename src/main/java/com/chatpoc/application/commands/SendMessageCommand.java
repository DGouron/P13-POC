package com.chatpoc.application.commands;

import java.util.UUID;

public record SendMessageCommand(
    UUID chatId,
    String content,
    String senderName,
    String senderEmail
) {}