/*
 * Discord4J - Unofficial wrapper for Discord API
 * Copyright (c) 2015
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package sx.blah.discord.handle.obj;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.DiscordEndpoints;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.DiscordUtils;
import sx.blah.discord.handle.impl.events.MessageUpdateEvent;
import sx.blah.discord.json.requests.MessageRequest;
import sx.blah.discord.json.responses.MessageResponse;
import sx.blah.discord.util.HTTP403Exception;
import sx.blah.discord.util.Requests;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author qt
 * @since 7:53 PM 16 Aug, 2015
 * Project: DiscordAPI
 * <p>
 * Stores relevant data about messages.
 */
public class Message {
    /**
     * The ID of the message. Used for message updating.
     */
    protected final String messageID;

    /**
     * The actual message (what you see
     * on your screen, the content).
     */
    protected String content;

	/**
	 * The User who sent the message.
	 */
	protected final User author;

    /**
     * The ID of the channel the message was sent in.
     */
    protected final Channel channel;

	/**
     * The time the message was received.
     */
    protected LocalDateTime timestamp;
	
	/**
	 * The client that created this object.
	 */
	protected final IDiscordClient client;

    public Message(IDiscordClient client, String messageID, String content, User user, Channel channel, LocalDateTime timestamp) {
        this.client = client;
		this.messageID = messageID;
        this.content = content;
	    this.author = user;
        this.channel = channel;
	    this.timestamp = timestamp;
    }

    // Getters and Setters. Boring.

    public String getContent() {
        return content;
    }
	
	public void setContent(String content) {
		this.content = content;
	}

    public Channel getChannel() {
        return channel;
    }

	public User getAuthor() {
		return author;
	}

    public String getID() {
        return messageID;
    }
	
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

    /**
     * Adds an @mention to the author of the referenced Message
     * object before your content
     * @param content Message to send.
     */
    public void reply(String content) throws IOException {
        getChannel().sendMessage(String.format("%s, %s", this.getAuthor(), content));
    }
	
	public Message edit(String content) {
		if (client.isReady()) {
			content = DiscordUtils.escapeString(content);
			
			try {
				MessageResponse response = DiscordUtils.GSON.fromJson(Requests.PATCH.makeRequest(DiscordEndpoints.CHANNELS + channel.getID() + "/messages/" + messageID,
						new StringEntity(DiscordUtils.GSON.toJson(new MessageRequest(content, new String[0])), "UTF-8"),
						new BasicNameValuePair("authorization", client.getToken()),
						new BasicNameValuePair("content-type", "application/json")), MessageResponse.class);
				
				Message oldMessage = new Message(client, this.messageID, this.content, author, channel, timestamp);
				this.content = response.content;
				this.timestamp = DiscordUtils.convertFromTimestamp(response.edited_timestamp);
				//Event dispatched here because otherwise there'll be an NPE as for some reason when the bot edits a message,
				// the event chain goes like this:
				//Original message edited to null, then the null message edited to the new content
				client.getDispatcher().dispatch(new MessageUpdateEvent(oldMessage, this));
			} catch (HTTP403Exception e) {
				Discord4J.LOGGER.error("Received 403 error attempting to send message; is your login correct?");
			}
			
		} else {
			Discord4J.LOGGER.error("Bot has not signed in yet!");
		}
		return this;
	}
	
	public void delete() {
		if (client.isReady()) {
			try {
				Requests.DELETE.makeRequest(DiscordEndpoints.CHANNELS + channel.getID() + "/messages/" + messageID,
						new BasicNameValuePair("authorization", client.getToken()));
			} catch (HTTP403Exception e) {
				Discord4J.LOGGER.error("Received 403 error attempting to delete message; is your login correct?");
			}
		} else {
			Discord4J.LOGGER.error("Bot has not signed in yet!");
		}
	}
}