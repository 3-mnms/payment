package com.teckit.payment.dto.api;

import com.teckit.payment.dto.request.CreateRequestDTO;
import com.teckit.payment.dto.request.PayByTekcitPayDTO;
import com.teckit.payment.dto.request.TransferRequestDTO;
import com.teckit.payment.dto.response.PaymentOrderDTO;
import com.teckit.payment.dto.response.TekcitPayAccountResponseDTO;
import com.teckit.payment.exception.global.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public interface TekcitPayAccountApiSpecification {

    @Tag(name = "POST", description = "테킷 페이 가입")
    @Operation(
            summary = "테킷 페이 가입 API",
            description = "비밀 번호는 String"
    )
    ResponseEntity<SuccessResponse<Void>> createTekcitPayAccount(
            @RequestBody CreateRequestDTO dto,
            @RequestHeader("X-User-Id") String userIdHeader);

    @Tag(name = "GET", description = "테킷 페이 조회")
    @Operation(
            summary = "테킷 페이 조회 API"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "404",
                    description = "테킷 페이 가입 이력 조회 불가",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "PaymentNotFound",
                                    summary = "userId에 해당하는 테킷 페이 가입자 조회 불가",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "NOT_FOUND_TEKCIT_PAY_ACCOUNT",
                                              "message": "테킷 페이에 가입되지 않은 사용자입니다."
                                            }
                                            """
                            )
                    )
            ),
    })
    ResponseEntity<SuccessResponse<TekcitPayAccountResponseDTO>> getTekcitPayAccount(@RequestHeader("X-User-Id") String userIdHeader);

    @Tag(name = "POST", description = "테킷 페이 결제")
    @Operation(
            summary = "테킷 페이 결제 API",
            description = "amount : 가격 , paymentId : 랜덤 발생, password : 비밀번호 (string) "
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "406",
                    description = "부적절한 입력값",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {@ExampleObject(
                                    name = "금액 문제",
                                    summary = "금액 음수 or 0 or 정수 아님",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_AMOUNT",
                                              "message": "유효하지 않은 금액입니다."
                                            }
                                            """
                            ), @ExampleObject(
                                    name = "잘못된 비밀번호",
                                    summary = "잘못된 비밀번호로 인한 오류",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_PASSWORD",
                                              "message": "일치하지 않는 비밀번호입니다."
                                            }
                                            """
                            ), @ExampleObject(
                                    name = "잘못된 비밀번호",
                                    summary = "잘못된 비밀번호로 인한 오류",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_PASSWORD",
                                              "message": "일치하지 않는 비밀번호입니다."
                                            }
                                            """
                            ), @ExampleObject(
                                    name = "잘못된 비밀번호",
                                    summary = "잘못된 비밀번호로 인한 오류",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_PASSWORD",
                                              "message": "일치하지 않는 비밀번호입니다."
                                            }
                                            """
                            ), @ExampleObject(
                                    name = "잘못된 비밀번호",
                                    summary = "잘못된 비밀번호로 인한 오류",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_PASSWORD",
                                              "message": "일치하지 않는 비밀번호입니다."
                                            }
                                            """
                            ), @ExampleObject(
                                    name = "잘못된 비밀번호",
                                    summary = "잘못된 비밀번호로 인한 오류",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_PASSWORD",
                                              "message": "일치하지 않는 비밀번호입니다."
                                            }
                                            """
                            ), @ExampleObject(
                                    name = "계정 불일치",
                                    summary = "사용자 계정과 일치하지 않는 테킷 페이 계좌",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "NOT_EQUAL_BUYER_ID_AND_USER_ID",
                                              "message": "결제 정보가 일치하지 않습니다."
                                            }
                                            """), @ExampleObject(
                                    name = "잔액 부족",
                                    summary = "결제 금액보다 부족한 테킷 페이 잔액 부족 ㅠ",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "NOT_ENOUGH_AVAILABLE_TEKCIT_PAY_POINT",
                                              "message": "충분하지 않은 테킷 페이 포인트입니다."
                                            }
                                            """)}
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "테킷 페이 가입 이력 조회 불가",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "PaymentNotFound",
                                    summary = "userId에 해당하는 테킷 페이 가입자 조회 불가",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "NOT_FOUND_TEKCIT_PAY_ACCOUNT",
                                              "message": "테킷 페이에 가입되지 않은 사용자입니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "결제 상태 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {@ExampleObject(
                                    summary = "POINT_PAYMENT가 아닐 때 (사실 이건 거의 오류 안남)",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_PAYMENT_STATUS",
                                              "message": "결제 정보가 일치하지 않습니다."
                                            }
                                            """
                            ),
                            }

                    )
            ),
    })
    ResponseEntity<SuccessResponse<Void>> payByTekcitPayAccount(
            @Valid @RequestBody PayByTekcitPayDTO dto, @RequestHeader("X-User-Id") String userIdHeader);


    @Tag(name = "GET", description = "테킷 페이 내역 조회")
    @Operation(
            summary = "테킷 페이 포인트 결제 내역 조회 API",
            description = "페이징 처리 돼 있음 "
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "404",
                    description = "테킷 페이 가입 이력 조회 불가",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "PaymentNotFound",
                                    summary = "userId에 해당하는 테킷 페이 가입자 조회 불가",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "NOT_FOUND_TEKCIT_PAY_ACCOUNT",
                                              "message": "테킷 페이에 가입되지 않은 사용자입니다."
                                            }
                                            """
                            )
                    )
            ),
    })
    ResponseEntity<SuccessResponse<Page<PaymentOrderDTO>>> getTekcitPayHistory(@RequestHeader("X-User-Id") String userIdHeader,
                                                                               @RequestParam(defaultValue = "0") int page,   // 기본값: 0
                                                                               @RequestParam(defaultValue = "10") int size);

    @Tag(name = "POST", description = "양도 API")
    @Operation(
            summary = "양도 API"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "406",
                    description = "셀프 양도",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {@ExampleObject(
                                    name = "셀프 양도",
                                    summary = "셀프 양도 불가 : 에러 메시지 저게 맞음 ",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "EQUALS_SELLER_BUYER",
                                              "message": "주최자는 주최측 공연을 구매할 수 없습니다."
                                            }
                                            """
                            ),@ExampleObject(
                                    name = "양도 금액 불일치 또는 수수료 금액",
                                    summary = "양도 금액 및 수수료 금액 불일치(더 많거나 더 적거나) ",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "INVALID_TRANSFER_AMOUNT",
                                              "message": "유효하지 않은 양도 금액입니다. 사기 ㄴㄴ"
                                            }
                                            """
                            )}
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "테킷 페이 가입 이력 조회 불가",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "PaymentNotFound",
                                    summary = "userId에 해당하는 테킷 페이 가입자 조회 불가 (판매자, 구매자,관리자)" +
                                            "오류 나면 판매자,구매자, 관리자(userId 1)로 가입되어 있는지 DB에서 확인할 것 ",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "NOT_FOUND_TEKCIT_PAY_ACCOUNT",
                                              "message": "테킷 페이에 가입되지 않은 사용자입니다."
                                            }
                                            """
                            )
                    )
            ),
    })

    ResponseEntity<SuccessResponse<Void>> transferToAnotherPerson(
            @Valid @RequestBody TransferRequestDTO dto,
            @RequestHeader("X-User-Id") String userIdHeader
    );
}
