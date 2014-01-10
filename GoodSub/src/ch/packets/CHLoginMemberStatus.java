package ch.packets;

import java.util.ArrayList;
import java.util.List;

import ch.connection.CHPacket;

public class CHLoginMemberStatus extends CHPacket {

	private static final long serialVersionUID = 1L;

	private List<String> members = new ArrayList<String>();

	public CHLoginMemberStatus(List<String> members) {
		this.members = members;
	}

	public List<String> getMembers() {
		return members;
	}
}