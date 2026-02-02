package io.github.eggy03.papertrail.sdk.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;


@Getter
@Builder(toBuilder = true)
public class MessageLogRegistrationEntity {

    @NotNull
    private final String guildId;

    @NotNull
    private final String channelId;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public MessageLogRegistrationEntity(@JsonProperty("guildId") @NonNull String guildId, @JsonProperty("channelId") @NonNull String channelId) {
        this.guildId=guildId;
        this.channelId=channelId;
    }
}
