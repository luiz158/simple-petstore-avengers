package test.support.com.pyxis.petstore.web;

import com.objogate.wl.UnsynchronizedProber;
import com.objogate.wl.web.AsyncWebDriver;
import org.openqa.selenium.WebDriver;
import test.support.com.pyxis.petstore.web.page.*;

import java.math.BigDecimal;

public class OldApplicationDriver {

    private final WebDriver browser;
    private final HomePage homePage;
    private final ProductsPage productsPage;
    private final ItemsPage itemsPage;
    private final CartPage cartPage;
    private final PurchasePage purchasePage;
    private final ReceiptPage receiptPage;
    private final Menu menu;

    public OldApplicationDriver(WebDriver webDriver) {
        this.browser = webDriver;
        AsyncWebDriver asyncDriver = new AsyncWebDriver(new UnsynchronizedProber(), webDriver);
        menu = new Menu(asyncDriver);
        homePage = new HomePage(asyncDriver);
        productsPage = new ProductsPage(asyncDriver);
        itemsPage = new ItemsPage(asyncDriver);
        cartPage = new CartPage(asyncDriver);
        purchasePage = new PurchasePage(asyncDriver);
        receiptPage = new ReceiptPage(asyncDriver);
    }

    public void open(Routing routes) {
        browser.navigate().to(routes.toHome());
    }

    public void logout() {
        menu.logout();
        homePage.displays();
    }

    public void close() {
        browser.close();
    }

    public void searchFor(String keyword) {
        menu.search(keyword);
        productsPage.displays();
    }

    public void showsNoResult() {
        productsPage.showsNoResult();
    }

    public void displaysNumberOfResults(int matchCount) {
        productsPage.displaysNumberOfResults(matchCount);
    }

    public void displaysProduct(String number, String name) {
        productsPage.displaysProduct(number, name);
    }

    public void consultInventoryOf(String product) {
        searchFor(product);
        browseInventory(product);
    }

    public void browseInventory(String product) {
        productsPage.browseItemsOf(product);
        itemsPage.displays();
    }

    public void showsNoItemAvailable() {
        itemsPage.showsNoItemAvailable();
    }

    public void displaysItem(String number, String description, String price) {
        itemsPage.displaysItem(number, description, price);
    }

    public void buy(String product, String itemNumber) {
        consultInventoryOf(product);
        buy(itemNumber);
    }

    public void buy(String itemNumber) {
        itemsPage.addToCart(itemNumber);
        cartPage.displays();
    }

    public void checkout() {
        cartPage.checkout();
        purchasePage.displays();
    }

    public void showsCartIsEmpty() {
        menu.showsCartIsEmpty();
    }

    public void showsCartTotalQuantity(int quantity) {
        menu.showsCartTotalQuantity(quantity);
    }

    public void showsItemInCart(String itemNumber, String itemDescription, String totalPrice) {
        cartPage.showsItemInCart(itemNumber, itemDescription, totalPrice);
    }

    public void showsItemQuantity(String itemNumber, int quantity) {
        cartPage.showsItemQuantity(itemNumber, quantity);
    }

    public void showsGrandTotal(String price) {
        cartPage.showsGrandTotal(price);
    }

    public void showsTotalToPay(String total) {
        purchasePage.showsTotalToPay(new BigDecimal(total));
    }

    public void pay(String firstName, String lastName, String email, String cardType, String cardNumber, String cardExpiryDate) {
        purchasePage.willBillTo(firstName, lastName, email);
        purchasePage.willPayUsingCreditCard(cardType, cardNumber, cardExpiryDate);
        purchasePage.confirmOrder();
        receiptPage.displays();
    }

    public void showsTotalPaid(String total) {
        receiptPage.showsTotalPaid(new BigDecimal(total));
    }

    public void showsLineItem(String itemNumber, String itemDescription, String totalPrice) {
        receiptPage.showsLineItem(itemNumber, itemDescription, totalPrice);
    }

    public void showsCreditCardDetails(String cardType, String cardNumber, String cardExpiryDate) {
        receiptPage.showsCreditCardDetails(cardType, cardNumber, cardExpiryDate);
    }

    public void showsBillingInformation(String firstName, String lastName, String emailAddress) {
        receiptPage.showsBillingInformation(firstName, lastName, emailAddress);
    }

    public void returnHome() {
        menu.home();
        homePage.displays();
    }

    public void continueShopping() {
        cartPage.continueShopping();
        homePage.displays();
    }

    public void reviewCart() {
        menu.cart();
        cartPage.displays();
    }
}
