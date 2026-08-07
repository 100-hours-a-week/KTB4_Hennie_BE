create table article_subscription (
    status bit not null,
    article_subscription_id bigint not null auto_increment,
    created_at datetime(6) not null,
    enterprise_id bigint not null,
    user_id bigint not null,
    primary key (article_subscription_id),
    index ix_article_subscription_enterprise_status (
        enterprise_id,
        status
    ),
    index ix_article_subscription_user_created (user_id, created_at),
    constraint uq_article_subscription_user_enterprise
        unique (user_id, enterprise_id),
    constraint fk_article_subscription_enterprise
        foreign key (enterprise_id)
        references enterprise (enterprise_id),
    constraint fk_article_subscription_user
        foreign key (user_id)
        references users (user_id)
) engine=InnoDB;
