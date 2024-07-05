/*
 * package com.quantum.ampmjobs.controller;
 *
 * import java.security.NoSuchAlgorithmException;
 *
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.RestController; import
 * org.springframework.web.servlet.view.RedirectView;
 *
 * import com.quantum.ampmjobs.api.utility.ApiResponse; import
 * com.quantum.ampmjobs.api.utility.PaymentApi;
 *
 * @RestController public class TmpController {
 *
 * @Autowired private PaymentApi api;
 *
 * @GetMapping("/fromJava") public RedirectView cpw() { String url =
 * "ampmjobs.in/ip"; try { ApiResponse pay = api.pay(); if (pay.isSuccess()) {
 * url = pay.getData().getInstrumentResponse().getRedirectInfo().getUrl(); }
 *
 * } catch (NoSuchAlgorithmException e) { e.printStackTrace(); } return new
 * RedirectView(url); }
 *
 * @GetMapping("/cc") public String cp() {
 *
 * return "pay-api"; }
 *
 * @GetMapping("/kk") public String kk() {
 * System.out.println("This is return from API "); return "pay-api"; } }
 */