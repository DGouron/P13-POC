package com.chatpoc.application.queries;

import java.util.UUID;

public record GetRecentMessagesQuery(UUID chatId, int limit) {}