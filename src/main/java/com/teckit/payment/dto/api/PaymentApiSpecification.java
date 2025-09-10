package com.teckit.payment.dto.api;

import com.teckit.payment.dto.request.PaymentRequestDTO;
import com.teckit.payment.dto.response.PaymentOrderDTO;
import com.teckit.payment.exception.global.SuccessResponse;
import com.teckit.payment.util.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface PaymentApiSpecification {
    @Tag(name = "POST", description = "환불")
    @Operation(
            summary = "payment id를 이용한 결제 환불 기능",
            description = "결제 환불 API"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "404",
                    description = "결제 식별자 미존재(결제 완료 X or 잘못된 paymentId)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "PaymentNotFound",
                                    summary = "paymentId를 찾을 수 없음",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "NOT_FOUND_PAYMENT_ID",
                                              "message": "존재하지 않는 결제 정보입니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "406",
                    description = "허용되지 않는 결제 정보",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            summary = "요청자와 구매자 불일치",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "NOT_EQUAL_BUYER_ID_AND_USER_ID",
                                                      "message": "결제 정보가 일치하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            summary = "결제가 완료되지 않은 결제 주문",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "NOT_PAID_ORDER",
                                                      "message": "결제되지 않은 주문입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            summary = "테킷 페이에 가입되지 않은 사용자일 때",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "NOT_FOUND_TEKCIT_PAY_ACCOUNT",
                                                      "message": "테킷 페이에 가입되지 않은 사용자입니다.."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400,401,403,404,500",
                    description = "일반 결제 환불시 PG 연동 실패 (PortOne 취소 실패 등)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "FailedPaymentCancel",
                                    summary = "PortOne 취소 API 실패",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "FAILED_PAYMENT_CANCEL",
                                              "message": "대충 영어로 뜸"
                                            }
                                            """
                            )
                    )
            ),
    })
    ResponseEntity<SuccessResponse<String>> paymentCancel(@PathVariable String paymentId,
                                                          @RequestHeader("X-User-Id") String userIdHeader);


    @Tag(name = "GET", description = "조회")
    @Operation(
            summary = "booking id 기반 주문 목록 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "404",
                    description = "결제 식별자 미존재(결제 완료 X or 잘못된 paymentId)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "PaymentNotFound",
                                    summary = "paymentId를 찾을 수 없음",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "NOT_FOUND_PAYMENT_ID",
                                              "message": "존재하지 않는 결제 정보입니다."
                                            }
                                            """
                            )
                    )
            ),
    })
    ResponseEntity<SuccessResponse<PaymentOrderDTO>> getPaymentOrderByBookingId(@PathVariable String bookingId,
                                                                                @RequestHeader("X-User-Id") String userIdHeader
    );

    @Tag(name = "POST", description = "결제 요청")
    @Operation(
            summary = "결제 요청 API",
            description = "paymentRequestType = " +
                    "일반 결제 : GENERAL_PAYMENT_REQUESTED  " +
                    "테킷 페이 결제 : POINT_PAYMENT_REQUESTED  " +
                    "포인트 충전 : POINT_CHARGE_REQUESTED" +
                    "포인트 충전의 경우에는 sellerId,festivalId 안넣어서 줘도 자동으로 들어갑니다."

    )
    ResponseEntity<SuccessResponse<String>> requestPayment(@RequestBody PaymentRequestDTO dto,
                                                           @RequestHeader("X-User-Id") String userIdHeader);

    @Tag(name = "POST", description = "결제 확인")
    @Operation(
            summary = "결제 완료 확인 API"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "404",
                    description = "결제 식별자 미존재(결제 완료 X or 잘못된 paymentId)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "PaymentNotFound",
                                    summary = "paymentId를 찾을 수 없음",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "NOT_FOUND_PAYMENT_ID",
                                              "message": "존재하지 않는 결제 정보입니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "포트원과 상충된 결제 정보",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {@ExampleObject(
                                    summary = "paymentId를 찾을 수 없음",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "NOT_INVALID_PAYMNET_BY_PORTONE",
                                              "message": "포트원에서 결제되지 않은 Payment ID입니다."
                                            }
                                            """
                            ),
                                    @ExampleObject(
                                            summary = "포트원 결제 정보와 금액 불일",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "NOT_EQUAL_CURRENCY_OR_AMOUNT",
                                                      "message": "금액이나 화폐 가치가 포트원 데이터와 일치하지 않습니다."
                                                    }
                                                    """
                                    ), @ExampleObject(
                                    summary = "정산되지 않은 PaymentId인 경우 (kafka 이벤트 손실일 확률 높음)",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "NOT_SETTLED_PAYMENT",
                                              "message": "정산되지 않은 Payment ID입니다."
                                            }
                                            """
                            )}

                    )
            ),
})

ResponseEntity<SuccessResponse<String>> completeConfirm(@PathVariable String paymentId);

}
