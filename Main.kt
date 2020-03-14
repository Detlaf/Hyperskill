package machine

import java.lang.IllegalStateException
import java.util.Scanner


fun main() {
    val scanner = Scanner(System.`in`)
    val machine = CoffeeMachine(400, 540, 120, 9, 550)
    var input = String()

    while (input != "exit") {
        input = scanner.next()
        machine.runAction(input)
    }
}

enum class CoffeeType(val water: Int, val milk: Int, val beans: Int, val price: Int) {
    Espresso(250, 0, 16, 4),
    Cappuccino(200, 100, 12, 6),
    Latte(350, 75, 20, 7)
}

enum class State {
    MAIN_MENU,
    CHOOSING_ACTION,
    BUYING,
    FILLING_WATER,
    FILLING_MILK,
    FILLING_BEANS,
    FILLING_CUPS
}

class CoffeeMachine(WATER: Int, MILK: Int, BEANS: Int, CUPS: Int, DOLLARS: Int) {
    private var nowWater: Int = 0
    private var nowMilk: Int = 0
    private var nowBeans: Int = 0
    private var nowCups: Int = 0
    private var nowDollars: Int = 0
    private var state: State = State.MAIN_MENU

    init {
        this.nowWater = if (WATER < 0) 0 else WATER
        this.nowMilk = if (MILK < 0) 0 else MILK
        this.nowBeans = if (BEANS < 0) 0 else BEANS
        this.nowCups = if (CUPS < 0) 0 else CUPS
        this.nowDollars = if (DOLLARS < 0) 0 else DOLLARS
        actionMainMenu()
    }

    fun runAction(input: String) {
        when (state) {
            State.CHOOSING_ACTION -> runEvent(input)
            State.BUYING -> buyCoffee(input)
            else -> fillMachine(input)
        }
    }

    private fun actionMainMenu() {
        print("Write action (buy, fill, take, remaining, exit): ")
        state = State.CHOOSING_ACTION
    }

    private fun runEvent(event: String) {
        when (event) {
            "buy" -> runBuyEvent()
            "fill" -> runFillEvent()
            "take" -> runTakeEvent()
            "remaining" -> runRemainingEvent()
        }
    }

    private fun runBuyEvent() {
        print("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ")
        state = State.BUYING
    }

    private fun buyCoffee(input: String) {
        when(input) {
            "1" -> makeCoffee(CoffeeType.Espresso)
            "2" -> makeCoffee(CoffeeType.Latte)
            "3" -> makeCoffee(CoffeeType.Cappuccino)
            "back" -> {}
        }
        actionMainMenu()
    }

    private  fun makeCoffee(coffeeType: CoffeeType) {
        if (haveEnoughResources(coffeeType)) {
            this.nowWater -= coffeeType.water
            this.nowMilk -= coffeeType.milk
            this.nowBeans -= coffeeType.beans
            this.nowCups -= 1
            this.nowDollars += coffeeType.price
            println("I have enough resources, making you a coffee!")
        } else {
            notEnoughResources(coffeeType)
        }
    }

    private fun haveEnoughResources(coffeeType: CoffeeType): Boolean {
        val enoughWater: Boolean = this.nowWater >= coffeeType.water
        val enoughMilk: Boolean = this.nowMilk >= coffeeType.milk
        val enoughBeans: Boolean = this.nowBeans >= coffeeType.beans
        val enoughCups: Boolean = this.nowCups >= 1

        return enoughWater && enoughMilk && enoughBeans && enoughCups
    }

    private fun notEnoughResources(coffeeType: CoffeeType) {
        when {
            this.nowWater < coffeeType.water -> println("Sorry, not enough water!")
            this.nowMilk < coffeeType.milk -> println("Sorry, not enough milk!")
            this.nowBeans < coffeeType.beans -> println("Sorry, not enough coffee beans!")
            this.nowCups < 1 -> println("Sorry, not enough disposable cups!")
        }
    }

    private fun runFillEvent() {
        state = State.FILLING_WATER
    }

    private fun fillMachine(input: String) {
        when (state) {
            State.FILLING_WATER -> {
                print("Write how many ml of water do you want to add: ")
                nowWater += input.toInt()
                state = State.FILLING_MILK
            }
            State.FILLING_MILK -> {
                print("Write how many ml of milk do you want to add:")
                nowMilk += input.toInt()
                state = State.FILLING_BEANS
            }
            State.FILLING_BEANS -> {
                print("Write how many grams of coffee beans do you want to add:")
                nowBeans += input.toInt()
                state = State.FILLING_CUPS
            }
            State.FILLING_CUPS -> {
                print("Write how many disposable cups of coffee do you want to add:")
                nowCups += input.toInt()
                actionMainMenu()
            }
            else -> throw IllegalStateException()
        }
    }

    private fun runTakeEvent() {
        println("I gave you $$nowDollars")
        nowDollars = 0
        actionMainMenu()
    }

    private fun runRemainingEvent() {
        println("The coffee machine has:")
        println("$nowWater of water")
        println("$nowMilk of milk")
        println("$nowBeans of beans")
        println("$nowCups of cups")
        println("$$nowDollars of money")
        actionMainMenu()
    }
}


