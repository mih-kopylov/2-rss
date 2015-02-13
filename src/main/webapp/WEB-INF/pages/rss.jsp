<?xml version="1.0" encoding="UTF-8"?>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="application/xml;charset=UTF-8" %>
<rss version="2.0">
    <channel>
        <title>${channel.title}</title>
        <link>${channel.link}</link>
        <description>${channel.description}</description>
        <lastBuildDate>${channel.lastBuildDate}</lastBuildDate>
        <pubDate>${channel.lastBuildDate}</pubDate>
        <c:forEach items="${channel.items}" var="item">
            <item>
                <title>${item.title}</title>
                <link>${item.link}</link>
                <pubDate>${item.pubDate}</pubDate>
                <description>${item.description}</description>
            </item>
        </c:forEach>
    </channel>
</rss>