package com.hennie.springdatajpa.domain.report.entity;

public enum ReportReason {

    SPAM("스팸홍보/도배글입니다."),
    SEXUAL_CONTENT("음란물입니다."),
    ILLEGAL_INFORMATION("불법정보를 포함하고 있습니다."),
    HARMFUL_TO_YOUTH("청소년에게 유해한 내용입니다."),
    HATE_SPEECH("욕설/생명경시/혐오/차별적 표현입니다."),
    PERSONAL_INFORMATION("개인정보 노출 게시물입니다."),
    OFFENSIVE_EXPRESSION("불쾌한 표현이 있습니다.");

    private final String description;

    ReportReason(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
