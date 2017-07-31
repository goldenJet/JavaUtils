package com.wailian.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the message_template database table.
 * 
 */
@Entity
@Data
@Table(name="smtp_config")

public class SMTPConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="email_address")
	private String emailAddress;

	@Column(name="smtp_address")
	private String smtpAddress;

	@Column(name="smtp_port")
	private int smtpPort;

	@Column(name="user_name")
	private String userName;

	private String password;

	public SMTPConfig() {
	}
}