rename table article_subscription to enterprise_subscription;

alter table enterprise_subscription
    rename column article_subscription_id to enterprise_subscription_id,
    rename index ix_article_subscription_enterprise_status
        to ix_enterprise_subscription_enterprise_status,
    rename index ix_article_subscription_user_created
        to ix_enterprise_subscription_user_created,
    rename index uq_article_subscription_user_enterprise
        to uq_enterprise_subscription_user_enterprise;

alter table enterprise_subscription
    drop foreign key fk_article_subscription_enterprise,
    drop foreign key fk_article_subscription_user,
    add constraint fk_enterprise_subscription_enterprise
        foreign key (enterprise_id)
        references enterprise (enterprise_id),
    add constraint fk_enterprise_subscription_user
        foreign key (user_id)
        references users (user_id);
