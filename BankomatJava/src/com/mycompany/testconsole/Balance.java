package com.mycompany.testconsole;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/*
при нескольких вариантах нет описания который выдавать и нет указаний выдавать максимальное количество денег или нет
 */
public class Balance {

    Map<Integer, Integer> nominal_count = new TreeMap<>();
    //private BigDecimal balance;

    public class NotEnoughMoneyException extends Exception {

    }

    public Balance(Map<Integer, Integer> nominal_count) {
        this.nominal_count = nominal_count;
    }

    public Balance(int a1, int a3, int a5, int a10, int a25, int a50, int a100, int a500, int a1000, int a5000) {
        nominal_count.put(1, a1);
        nominal_count.put(3, a3);
        nominal_count.put(5, a5);
        nominal_count.put(10, a10);
        nominal_count.put(25, a25);
        nominal_count.put(50, a50);
        nominal_count.put(100, a100);
        nominal_count.put(500, a500);
        nominal_count.put(1000, a1000);
        nominal_count.put(5000, a5000);

    }

    public synchronized void Put(Integer nominal, Integer count) {
        for (int index = 0; index < nominal_count.keySet().size(); index++) {
            if (nominal.equals(nominal_count.keySet().toArray()[index])) {
                Integer key = (Integer) nominal_count.keySet().toArray()[index];
                Integer value = nominal_count.get(key);
                nominal_count.replace(key, value + count);
                break;
            }
        }

    }

    public synchronized void Dump() {
        for (Integer key : nominal_count.keySet()) {
            System.out.println(key + " " + nominal_count.get(key));
        }

    }

    private static Map<Integer, Integer> FindPayVar(Map<Integer,Integer> nominal_cont, int amount) {//можно сделать основным методом get если j итерировать с 0
        //и для ускорения не рассматривать случаи ухода в отрицательные числа
        int given_amount;
        Map<Integer, Integer> given_map = new TreeMap();

        for (int i = (nominal_cont.size() - 1); i >= 0; i--) {
            for (int j = 1; j < 5; j++) {
                given_amount = 0;
                Map<Integer, Integer> try_nominal_map = new TreeMap();
                try_nominal_map.putAll(nominal_cont);
                try_nominal_map.replace((Integer) try_nominal_map.keySet().toArray()[i],
                        try_nominal_map.get(try_nominal_map.keySet().toArray()[i]) - j);
                given_map = TryVar(try_nominal_map, amount);
                for (int nominal : given_map.keySet()) {
                    given_amount += nominal * given_map.get(nominal);
                }
                if (amount == given_amount) {
                    return given_map;
                }
            }
        }
        return null;

    }

    private static Map<Integer, Integer> TryVar(Map<Integer, Integer> nominal_map, int amount) {
        Map<Integer, Integer> givenMoney = new TreeMap<>();
        int nominal;
        int count;
        if (nominal_map.keySet().isEmpty()) {
            System.out.println("нет списка номиналов");
            return null;
        }
        for (int index_withot_comparator = nominal_map.keySet().size() - 1; index_withot_comparator >= 0; index_withot_comparator--) {
            nominal = (Integer) nominal_map.keySet().toArray()[index_withot_comparator];
            count = GiveMax(nominal_map, amount, nominal);
            givenMoney.put(nominal, count);
            amount -= count * nominal;
        }
        return givenMoney;

    }

    public static long State(Map<Integer, Integer> nominal_count) {
        long state_balance = 0;

        for (Integer key : nominal_count.keySet()) {
            state_balance += (key * nominal_count.get(key));
        }
        return state_balance;
    }

    public synchronized long State() {
        long state_balance = 0;

        for (Integer key : nominal_count.keySet()) {
            state_balance += (key * nominal_count.get(key));
        }
        return state_balance;
    }

    public synchronized void Get(int amount) {
        int start_amount = amount;
        Map<Integer, Integer> givenMoney = new TreeMap<>();
        Map<Integer, Integer> try_givenMoney = new TreeMap<>();
        int nominal;
        int count;
        if (nominal_count.keySet().isEmpty()) {
            System.out.println("нет списка номиналов");
            return;
        }
        for (int index_withot_comparator = nominal_count.keySet().size() - 1; index_withot_comparator >= 0; index_withot_comparator--) {
            nominal = (Integer) nominal_count.keySet().toArray()[index_withot_comparator];
            count = GiveMax(nominal_count, amount, nominal);
            givenMoney.put(nominal, count);
            amount -= count * nominal;
        }
        if ((State(givenMoney) != start_amount) && (State(nominal_count) > start_amount)) //если деньги в банкомате не кончились, но не выдалось все
        {
            try_givenMoney = FindPayVar(nominal_count, start_amount);
            if (try_givenMoney != null) 
                printForGet(try_givenMoney, start_amount);
            else 
                printForGet(givenMoney, start_amount);
        }
        else printForGet(givenMoney, start_amount);
        
                
            

    }
    
    private void printForGet(Map<Integer, Integer> givenMoney, int start_amount) {
        nominal_count.keySet().stream().forEach((nom) -> {
            nominal_count.replace(nom, nominal_count.get(nom) - givenMoney.get(nom));
            if ((givenMoney.get(nom)) != 0) {
                System.out.print(nom + "=" + givenMoney.get(nom) + ",");
            }
        });
        System.out.println("всего " + State(givenMoney));
        if (State(givenMoney) != start_amount) {
            System.out.println("без " + (start_amount - State(givenMoney)));
        }
    }
    
    private static int GiveMax(Map<Integer, Integer> nominal_map, int amount, int nominal) {
        int value = 0;
        int given_count = 0;
        while (amount >= (value + nominal)) {
            if (nominal_map.get(nominal) > given_count) {
                given_count += 1;
                value = nominal * given_count;
            } else {
                break;
            }
        }
        return given_count;
    }

    public static void main(String[] args) {
        Balance bal = new Balance(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        int nominal;
        int count;
        int amount;
        Scanner newscan = new Scanner(System.in);
        String input = "";
        while (!input.equals("exit")) {
            input = newscan.next();
            if (input.equals("put")) {
                nominal = newscan.nextInt();
                if (!bal.nominal_count.keySet().contains(nominal)) {
                    System.out.println("invalid nominal");
                    break;
                }
                count = newscan.nextInt();
                bal.Put(nominal, count);
                System.out.println(bal.State());
            }
            if (input.equals("get")) {
                amount = newscan.nextInt();
                bal.Get(amount);
            }
            if (input.equals("dump")) {
                bal.Dump();
            }
            if (input.equals("state")) {
                System.out.println(bal.State());
            }

        }

    }
}
