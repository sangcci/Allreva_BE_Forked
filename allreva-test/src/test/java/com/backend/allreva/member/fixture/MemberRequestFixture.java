package com.backend.allreva.member.fixture;

import static org.instancio.Select.field;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.member.command.input.MemberInfoUpdateCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberRequestFixture {

    public static Model<MemberInfoUpdateCommand> memberInfoUpdateCommandModel() {
        return Instancio.of(MemberInfoUpdateCommand.class)
                .set(field(MemberInfoUpdateCommand.class, "image"), new Image("https://example.com/profile.png"))
                .toModel();
    }
}
