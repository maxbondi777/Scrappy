package com.scrappy.scrappy.service.project;

import com.scrappy.scrappy.controller.dto.project.InviteDTO;
import com.scrappy.scrappy.controller.dto.project.MemberDTO;
import lombok.Data;

import java.util.List;

@Data
public class MembersResponseDTO {
    private List<MemberDTO> activeMembers;
    private List<InviteDTO> pendingInvites;
}
