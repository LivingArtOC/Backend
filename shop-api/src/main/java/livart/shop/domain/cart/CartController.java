package livart.shop.domain.cart;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import livart.common.Auth.CustomUserDetails;
import livart.shop.domain.cart.dto.request.AddItemRequest;
import livart.shop.domain.cart.dto.request.UpdateOptionsRequest;
import livart.shop.domain.cart.dto.request.UpdateQuantityRequest;
import livart.shop.domain.cart.dto.response.CartItemResponse;
import livart.shop.domain.cart.dto.response.CartListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "장바구니 API ")
@RequestMapping("/api/shop/cart")
public class CartController {

    private final CartService cartService;

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
        return cud.getId();
    }

    /** 장바구니 목록 조회 (쿠키는 수신만, 권한은 로그인 사용자 기준) */
    @GetMapping
    public CartListResponse getCart(@CookieValue(name = "X-CART-ID", required = false) String cartCookie) {
        return cartService.getCart(currentUserId());
    }

    /** 담기 */
    @PostMapping("/items")
    public CartItemResponse addItem(@RequestBody @Valid AddItemRequest req) {
        return cartService.addItem(currentUserId(), req);
    }

    /** 수량 변경 */
    @PatchMapping("/items/{itemId}/quantity")
    public CartItemResponse updateQuantity(@PathVariable Long itemId,
                                           @RequestBody @Valid UpdateQuantityRequest req) {
        return cartService.updateQuantity(currentUserId(), itemId, req.quantity());
    }

    /** 특정 아이템 옵션 조회 */
    @GetMapping("/items/{itemId}/options")
    public UpdateOptionsRequest getOptions(@PathVariable Long itemId) {
        return new UpdateOptionsRequest(cartService.getOptionValueIds(currentUserId(), itemId));
    }

    /** 특정 아이템 옵션 수정 */
    @PutMapping("/items/{itemId}/options")
    public CartItemResponse updateOptions(@PathVariable Long itemId,
                                          @RequestBody @Valid UpdateOptionsRequest req) {
        return cartService.updateOptions(currentUserId(), itemId, req.optionValueIds());
    }

    /** 단건 삭제 */
    @DeleteMapping("/items/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        cartService.deleteItem(currentUserId(), itemId);
    }

    /** 전체 삭제 */
    @DeleteMapping("/items")
    public void deleteAll() {
        cartService.deleteAll(currentUserId());
    }
}