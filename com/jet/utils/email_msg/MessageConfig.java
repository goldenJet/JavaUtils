package com.jet.utils.email_msg;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the message_template database table.
 * 
 */
@Entity
@Data
@Table(name="message_config")

public class MessageConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="user_name")
	private String userName;

	private String password;

	@Column(name="interface_address")
	private String interfaceAddress;

	private String sprdid;

	private String scorpid;

	public MessageConfig() {
	}
}