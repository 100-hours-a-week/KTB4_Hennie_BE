create table enterprise (
    status bit not null,
    created_at datetime(6) not null,
    enterprise_id bigint not null auto_increment,
    crawl_source varchar(30) not null,
    name varchar(100) not null,
    primary key (enterprise_id),
    constraint uq_enterprise_crawl_source unique (crawl_source)
) engine=InnoDB;

insert into enterprise (
    enterprise_id,
    crawl_source,
    name,
    status,
    created_at
)
values
    (1, 'KAKAO', '카카오', true, current_timestamp(6)),
    (2, 'NAVER_D2', '네이버 D2', true, current_timestamp(6)),
    (3, 'TOSS', '토스', true, current_timestamp(6)),
    (4, 'COUPANG', '쿠팡', true, current_timestamp(6)),
    (5, 'WOOWA', '우아한형제들', true, current_timestamp(6)),
    (6, 'DAANGN', '당근', true, current_timestamp(6)),
    (7, 'OLIVE_YOUNG', '올리브영', true, current_timestamp(6));

alter table tech_article
    add column enterprise_id bigint null after tech_article_id;

update tech_article ta
join enterprise e on e.crawl_source = ta.enterprise
set ta.enterprise_id = e.enterprise_id;

alter table tech_article
    modify column enterprise_id bigint not null,
    add index ix_tech_article_enterprise (enterprise_id),
    add constraint fk_tech_article_enterprise
        foreign key (enterprise_id)
        references enterprise (enterprise_id),
    drop column enterprise;
