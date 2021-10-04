package com.example.WebIssueTracker.services;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Service;

@Service
public class ClearService {

    public String sanitizeHTML(String untrustedHTML){
        PolicyFactory policy = new HtmlPolicyBuilder()
                .allowStandardUrlProtocols()
                .allowElements(
                        "i", "b","br","u","ul","li","h1","h2","h3","h4","h5","h6","a","img")
                .allowAttributes("href","style","src")
                .onElements("p","a","img")
                .toFactory();
        return policy.sanitize(untrustedHTML);
    }
}
