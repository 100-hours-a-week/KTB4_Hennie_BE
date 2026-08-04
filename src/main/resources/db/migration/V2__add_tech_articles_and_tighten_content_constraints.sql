-- Preserve the original free-form report reason before converting it to the
-- finite set accepted by the current API. Hibernate ignores this legacy-only
-- column, but it prevents information loss for reports created before V2.
alter table report
    add column legacy_reason varchar(255) null after reason;

update report
set legacy_reason = reason;

update report
set reason = case
    when reason in (
        'SPAM',
        'SEXUAL_CONTENT',
        'ILLEGAL_INFORMATION',
        'HARMFUL_TO_YOUTH',
        'HATE_SPEECH',
        'PERSONAL_INFORMATION',
        'OFFENSIVE_EXPRESSION'
    ) then reason
    when reason = '스팸홍보/도배글입니다.' then 'SPAM'
    when reason = '음란물입니다.' then 'SEXUAL_CONTENT'
    when reason = '불법정보를 포함하고 있습니다.' then 'ILLEGAL_INFORMATION'
    when reason = '청소년에게 유해한 내용입니다.' then 'HARMFUL_TO_YOUTH'
    when reason = '욕설/생명경시/혐오/차별적 표현입니다.' then 'HATE_SPEECH'
    when reason = '개인정보 노출 게시물입니다.' then 'PERSONAL_INFORMATION'
    else 'OFFENSIVE_EXPRESSION'
end;

update report
set type = case
        when post_id is not null then 'POST'
        else 'USER'
    end
where type is null;

update report
set status = 'PENDING'
where status is null;

alter table report
    modify column reason enum (
        'HARMFUL_TO_YOUTH',
        'HATE_SPEECH',
        'ILLEGAL_INFORMATION',
        'OFFENSIVE_EXPRESSION',
        'PERSONAL_INFORMATION',
        'SEXUAL_CONTENT',
        'SPAM'
    ) not null,
    modify column status enum ('ACCEPTED','PENDING','REJECTED') not null,
    modify column type enum ('POST','USER') not null;

alter table comment
    modify column content varchar(3000);

alter table post
    modify column title varchar(100) not null,
    add column tech_article_id bigint null after post_id;

create table tech_article (
    crawled_at datetime(6) not null,
    published_at datetime(6),
    tech_article_id bigint not null auto_increment,
    title varchar(300) not null,
    original_url varchar(768) not null,
    enterprise enum (
        'COUPANG',
        'DAANGN',
        'KAKAO',
        'NAVER_D2',
        'OLIVE_YOUNG',
        'TOSS',
        'WOOWA'
    ) not null,
    primary key (tech_article_id),
    constraint tech_article_original_url unique (original_url)
) engine=InnoDB;

create index post_tech_article_id
    on post (tech_article_id);

alter table post
    add constraint fk_post_tech_article
        foreign key (tech_article_id)
        references tech_article (tech_article_id);
