package webengdus.shopservice

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.view.RedirectView

@Controller
@RequestMapping("/store")
class StoreController(
        private val productService: ProductService,
        private val productRepository: ProductRepository,
        private val analyticsService: PurchaseAnalyticsService) {

    @GetMapping("index.html")
    fun storeOverview(model: Model): String {
        val products = productService.getProducts()
        model.addAttribute("products", products)
        return "storefront"
    }

    @GetMapping("thanks.html")
    fun storeThanks(): String {
        return "thanks"
    }

    @GetMapping("show/{sku}.html")
    fun productDetail(@PathVariable sku: Long, model: Model): String {
        model.addAttribute("product", productService.getProduct(sku))
        return "product-detail"
    }

    @GetMapping("buy/{sku}")
    fun stockChange(@PathVariable sku: Long): RedirectView {
        val product = productRepository.getOne(sku)
        product.stock = if (product.stock > 1) product.stock - 1 else 0
        productRepository.save(product)
        analyticsService.emitRecord(AnalyticsRecord(RequestContextHolder.currentRequestAttributes().sessionId, sku))
        return RedirectView("/store/thanks.html")
    }
}