create table notification (
    article_id bigint null,
    actor_id bigint null,
    comment_id bigint null,
    created_at datetime(6) not null,
    enterprise_id bigint null,
    notification_id bigint not null auto_increment,
    post_id bigint null,
    read_at datetime(6) null,
    recipient_id bigint not null,
    message text not null,
    notification_type varchar(50) not null,
    primary key (notification_id),
    index ix_notification_recipient_created (recipient_id, created_at),
    index ix_notification_recipient_read_created (recipient_id, read_at, created_at),
    constraint uq_notification_recipient_type_comment
        unique (recipient_id, notification_type, comment_id),
    constraint uq_notification_recipient_type_article
        unique (recipient_id, notification_type, article_id),
    constraint ck_notification_type check (
        notification_type in (
            'POST_COMMENT',
            'COMMENT_REPLY',
            'REPLY_REPLY',
            'SUBSCRIBED_ENTERPRISE_ARTICLE'
        )
    ),
    constraint fk_notification_recipient
        foreign key (recipient_id)
        references users (user_id),
    constraint fk_notification_actor
        foreign key (actor_id)
        references users (user_id)
        on delete set null,
    constraint fk_notification_post
        foreign key (post_id)
        references post (post_id)
        on delete set null,
    constraint fk_notification_comment
        foreign key (comment_id)
        references comment (comment_id)
        on delete set null,
    constraint fk_notification_article
        foreign key (article_id)
        references tech_article (tech_article_id)
        on delete set null,
    constraint fk_notification_enterprise
        foreign key (enterprise_id)
        references enterprise (enterprise_id)
        on delete set null
) engine=InnoDB;
