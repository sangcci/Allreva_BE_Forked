package com.backend.allreva.member.command.application.request;

public record MemberArtistRequest(
        String spotifyArtistId,
        String name) {
}
