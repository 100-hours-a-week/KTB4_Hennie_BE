
    create table comment (
        deleted bit not null,
        edited bit not null,
        comment_id bigint not null auto_increment,
        created_at datetime(6) not null,
        modified_at datetime(6) not null,
        parent_id bigint,
        post_id bigint not null,
        reply_to_id bigint,
        user_id bigint not null,
        content varchar(3000),
        primary key (comment_id)
    ) engine=InnoDB;

    create table post (
        blinded bit not null,
        edited bit not null,
        report_count integer not null,
        view_count integer not null,
        created_at datetime(6) not null,
        modified_at datetime(6) not null,
        post_id bigint not null auto_increment,
        tech_article_id bigint,
        user_id bigint not null,
        title varchar(100) not null,
        content TEXT not null,
        category enum ('AI','BE','FE'),
        status enum ('DRAFT','PUBLISHED') not null,
        primary key (post_id)
    ) engine=InnoDB;

    create table post_edit_history (
        edited_at datetime(6) not null,
        editor_id bigint,
        post_edit_hist_id bigint not null auto_increment,
        post_id bigint,
        after_content TEXT,
        after_title varchar(255),
        before_content TEXT,
        before_title varchar(255),
        after_category enum ('AI','BE','FE'),
        before_category enum ('AI','BE','FE'),
        primary key (post_edit_hist_id)
    ) engine=InnoDB;

    create table post_likes (
        created_at datetime(6) not null,
        post_id bigint not null,
        post_likes_id bigint not null auto_increment,
        user_id bigint not null,
        primary key (post_likes_id)
    ) engine=InnoDB;

    create table post_views (
        created_at datetime(6) not null,
        post_id bigint not null,
        post_view_id bigint not null auto_increment,
        user_id bigint not null,
        primary key (post_view_id)
    ) engine=InnoDB;

    create table refresh_token (
        expires_at datetime(6) not null,
        id bigint not null auto_increment,
        user_id bigint not null,
        token varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table report (
        created_at datetime(6) not null,
        post_id bigint,
        report_id bigint not null auto_increment,
        reporter_id bigint not null,
        target_user_id bigint,
        reason enum ('HARMFUL_TO_YOUTH','HATE_SPEECH','ILLEGAL_INFORMATION','OFFENSIVE_EXPRESSION','PERSONAL_INFORMATION','SEXUAL_CONTENT','SPAM') not null,
        status enum ('ACCEPTED','PENDING','REJECTED') not null,
        type enum ('POST','USER') not null,
        primary key (report_id)
    ) engine=InnoDB;

    create table tech_article (
        crawled_at datetime(6) not null,
        published_at datetime(6),
        tech_article_id bigint not null auto_increment,
        title varchar(300) not null,
        original_url varchar(768) not null,
        enterprise enum ('COUPANG','DAANGN','KAKAO','NAVER_D2','OLIVE_YOUNG','TOSS','WOOWA') not null,
        primary key (tech_article_id)
    ) engine=InnoDB;

    create table users (
        author_deleted bit not null,
        created_at datetime(6) not null,
        modified_at datetime(6) not null,
        user_id bigint not null auto_increment,
        password varchar(60) not null,
        email varchar(255) not null,
        nickname varchar(255) not null,
        profile_url varchar(255),
        primary key (user_id)
    ) engine=InnoDB;

    create index post_tech_article_id 
       on post (tech_article_id);

    alter table post_likes 
       add constraint uq_post_like unique (post_id, user_id);

    alter table post_views 
       add constraint uq_post_view unique (post_id, user_id);

    alter table refresh_token 
       add constraint UKr4k4edos30bx9neoq81mdvwph unique (token);

    alter table report 
       add constraint uq_report_post unique (reporter_id, post_id);

    alter table report 
       add constraint uq_report_user unique (reporter_id, target_user_id);

    alter table tech_article 
       add constraint tech_article_original_url unique (original_url);

    alter table users 
       add constraint uq_user_email unique (email);

    alter table users 
       add constraint uq_user_nickname unique (nickname);

    alter table comment 
       add constraint FKqm52p1v3o13hy268he0wcngr5 
       foreign key (user_id) 
       references users (user_id);

    alter table comment 
       add constraint FKde3rfu96lep00br5ov0mdieyt 
       foreign key (parent_id) 
       references comment (comment_id);

    alter table comment 
       add constraint FKs1slvnkuemjsq2kj4h3vhx7i1 
       foreign key (post_id) 
       references post (post_id);

    alter table comment 
       add constraint FKqys9mdo2xp2p1848yk002bw8e 
       foreign key (reply_to_id) 
       references comment (comment_id);

    alter table post 
       add constraint FK7ky67sgi7k0ayf22652f7763r 
       foreign key (user_id) 
       references users (user_id);

    alter table post 
       add constraint fk_post_tech_article 
       foreign key (tech_article_id) 
       references tech_article (tech_article_id);

    alter table post_likes 
       add constraint FKmxmoc9p5ndijnsqtvsjcuoxm3 
       foreign key (post_id) 
       references post (post_id);

    alter table post_likes 
       add constraint FKkgau5n0nlewg6o9lr4yibqgxj 
       foreign key (user_id) 
       references users (user_id);

    alter table post_views 
       add constraint FK8a1o0yvf8ee1udcj1vp8k5d22 
       foreign key (post_id) 
       references post (post_id);

    alter table post_views 
       add constraint FKiiwykhlbhjwi5cxxcx9n76cd6 
       foreign key (user_id) 
       references users (user_id);

    alter table report 
       add constraint FKnuqod1y014fp5bmqjeoffcgqy 
       foreign key (post_id) 
       references post (post_id);

    alter table report 
       add constraint FKqbhdxqd3ly7fkhly5nrl2j93k 
       foreign key (reporter_id) 
       references users (user_id);

    alter table report 
       add constraint FK14hrtq6fw0l01spmrbpvrep62 
       foreign key (target_user_id) 
       references users (user_id);
