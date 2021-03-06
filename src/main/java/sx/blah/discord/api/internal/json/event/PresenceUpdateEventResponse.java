/*
 *     This file is part of Discord4J.
 *
 *     Discord4J is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Discord4J is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Discord4J.  If not, see <http://www.gnu.org/licenses/>.
 */

package sx.blah.discord.api.internal.json.event;


import sx.blah.discord.api.internal.json.objects.GameObject;
import sx.blah.discord.api.internal.json.objects.UserObject;

/**
 * This is received when a user's presence changes
 */
public class PresenceUpdateEventResponse {

	/**
	 * The user this represents
	 */
	public UserObject user;

	/**
	 * The status for the user, either: "idle" or "online"
	 */
	public String status;

	/**
	 * The game the user is playing (or null if no game being played)
	 */
	public GameObject game;

	/**
	 * The roles the user is a part of
	 */
	public String[] roles;

	/**
	 * The guild the presence updated in
	 */
	public String guild_id;
}
