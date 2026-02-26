-- Products from out.csv (Vkusvill), idempotent: skip if url exists
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Налистники "Конвертики" со сливочным сыром и филе форели', 'https://vkusvill.ru/goods/nalistniki-konvertiki-so-slivochnym-syrom-i-file-foreli-115876.html', 318, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/nalistniki-konvertiki-so-slivochnym-syrom-i-file-foreli-115876.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Конвертики с курицей, шпинатом и сыром', 'https://vkusvill.ru/goods/konvertiki-s-kuritsey-shpinatom-i-syrom-109601.html', 285, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/konvertiki-s-kuritsey-shpinatom-i-syrom-109601.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Штрудель с вишней и яблоком', 'https://vkusvill.ru/goods/shtrudel-s-vishney-i-yablokom-80132.html', 176, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/shtrudel-s-vishney-i-yablokom-80132.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Картофельные дольки с мясным фаршем, беконом и маринованными огурцами, кафе', 'https://vkusvill.ru/goods/kartofelnye-dolki-s-myasnym-farshem-bekonom-i-marinovannymi-ogurtsami-kafe-111731.html', 390, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kartofelnye-dolki-s-myasnym-farshem-bekonom-i-marinovannymi-ogurtsami-kafe-111731.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Корн-дог Сочиз', 'https://vkusvill.ru/goods/korn-dog-sochiz-264165.html', 290, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/korn-dog-sochiz-264165.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Креветки в темпуре', 'https://vkusvill.ru/goods/krevetki-v-tempure-258109.html', 450, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/krevetki-v-tempure-258109.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Кани темпура ролл', 'https://vkusvill.ru/goods/kani-tempura-roll-258111.html', 410, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kani-tempura-roll-258111.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Бургер с говядиной Барбекю', 'https://vkusvill.ru/goods/burger-s-govyadinoy-barbekyu-43103.html', 560, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/burger-s-govyadinoy-barbekyu-43103.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Сэндвич ролл с индейкой и клюквой', 'https://vkusvill.ru/goods/sendvich-roll-s-indeykoy-i-klyukvoy-116366.html', 278, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/sendvich-roll-s-indeykoy-i-klyukvoy-116366.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Закуска "Имам Баялды" из баклажанов', 'https://vkusvill.ru/goods/zakuska-imam-bayaldy-iz-baklazhanov-23039.html', 265, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/zakuska-imam-bayaldy-iz-baklazhanov-23039.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Морская капуста с натуральным мясом камчатского краба 300г', 'https://vkusvill.ru/goods/morskaya-kapusta-s-naturalnym-myasom-kamchatskogo-kraba-300g-104848.html', 750, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/morskaya-kapusta-s-naturalnym-myasom-kamchatskogo-kraba-300g-104848.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Комбо: Лосось стейк с кожей в маринаде на гриле и салат Витаминный с лимонной заправкой', 'https://vkusvill.ru/goods/kombo-losos-steyk-s-kozhey-v-marinade-na-grile-i-salat-vitaminnyy-s-limonnoy-zapravkoy-98435.html', 995, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kombo-losos-steyk-s-kozhey-v-marinade-na-grile-i-salat-vitaminnyy-s-limonnoy-zapravkoy-98435.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Салат из свеклы с черносливом и грецким орехом, 150 г', 'https://vkusvill.ru/goods/salat-iz-svekly-s-chernoslivom-i-gretskim-orekhom-150-g-107373.html', 173, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/salat-iz-svekly-s-chernoslivom-i-gretskim-orekhom-150-g-107373.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Суп щавелевый с курицей и сметаной', 'https://vkusvill.ru/goods/sup-shchavelevyy-s-kuritsey-i-smetanoy-78990.html', 345, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/sup-shchavelevyy-s-kuritsey-i-smetanoy-78990.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Хумус "Иерусалимский"', 'https://vkusvill.ru/goods/khumus-ierusalimskiy-53716.html', 238, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/khumus-ierusalimskiy-53716.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Куриные сердечки жареные с луком', 'https://vkusvill.ru/goods/kurinye-serdechki-zharenye-s-lukom-95045.html', 258, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kurinye-serdechki-zharenye-s-lukom-95045.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Куриная грудка су-вид с гречкой', 'https://vkusvill.ru/goods/kurinaya-grudka-su-vid-s-grechkoy-112683.html', 274, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kurinaya-grudka-su-vid-s-grechkoy-112683.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Яблочно-клубничный штрудель', 'https://vkusvill.ru/goods/yablochno-klubnichnyy-shtrudel-105328.html', 260, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/yablochno-klubnichnyy-shtrudel-105328.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Расстегаи с лососем и палтусом, 120 г (2 шт)', 'https://vkusvill.ru/goods/rasstegai-s-lososem-i-paltusom-120-g-2-sht-46828.html', 200, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/rasstegai-s-lososem-i-paltusom-120-g-2-sht-46828.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Гречка по-купечески', 'https://vkusvill.ru/goods/grechka-po-kupecheski-66903.html', 238, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/grechka-po-kupecheski-66903.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Деревенский завтрак', 'https://vkusvill.ru/goods/derevenskiy-zavtrak-65057.html', 465, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/derevenskiy-zavtrak-65057.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Картофель мини с чесноком, зеленью запеченный', 'https://vkusvill.ru/goods/kartofel-mini-s-chesnokom-zelenyu-zapechennyy-100604.html', 308, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kartofel-mini-s-chesnokom-zelenyu-zapechennyy-100604.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Голубцы ленивые из мяса птицы', 'https://vkusvill.ru/goods/golubtsy-lenivye-iz-myasa-ptitsy-107205.html', 231, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/golubtsy-lenivye-iz-myasa-ptitsy-107205.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Ассорти закусок', 'https://vkusvill.ru/goods/assorti-zakusok-116101.html', 560, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/assorti-zakusok-116101.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Бутерброд с тунцом и базиликом, 156 г', 'https://vkusvill.ru/goods/buterbrod-s-tuntsom-i-bazilikom-156-g-86021.html', 258, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/buterbrod-s-tuntsom-i-bazilikom-156-g-86021.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Большая порция блинчики с вареной сгущенкой', 'https://vkusvill.ru/goods/bolshaya-portsiya-blinchiki-s-varenoy-sgushchenkoy-96343.html', 778, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/bolshaya-portsiya-blinchiki-s-varenoy-sgushchenkoy-96343.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Аге эби сандочи', 'https://vkusvill.ru/goods/age-ebi-sandochi-258132.html', 495, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/age-ebi-sandochi-258132.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Сэндвич-круассан со сливочным муссом из кеты', 'https://vkusvill.ru/goods/sendvich-kruassan-so-slivochnym-mussom-iz-kety-115877.html', 288, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/sendvich-kruassan-so-slivochnym-mussom-iz-kety-115877.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Сэндвич ролл с курицей "Тандури"', 'https://vkusvill.ru/goods/sendvich-roll-s-kuritsey-tanduri-38114.html', 258, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/sendvich-roll-s-kuritsey-tanduri-38114.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Ролл Сяке темпура', 'https://vkusvill.ru/goods/roll-syake-tempura-258113.html', 490, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/roll-syake-tempura-258113.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Голубцы ленивые в сметанном соусе', 'https://vkusvill.ru/goods/golubtsy-lenivye-v-smetannom-souse-29522.html', 238, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/golubtsy-lenivye-v-smetannom-souse-29522.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Крем-суп с лисичками и белыми грибами', 'https://vkusvill.ru/goods/krem-sup-s-lisichkami-i-belymi-gribami-98575.html', 308, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/krem-sup-s-lisichkami-i-belymi-gribami-98575.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Сэндвич ролл "Латиноамериканский" с соусом ранч', 'https://vkusvill.ru/goods/sendvich-roll-latinoamerikanskiy-s-sousom-ranch-114713.html', 294, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/sendvich-roll-latinoamerikanskiy-s-sousom-ranch-114713.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Хот-Дог Классический', 'https://vkusvill.ru/goods/khot-dog-klassicheskiy-70429.html', 352, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/khot-dog-klassicheskiy-70429.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Грузинский суп из фасоли с овощами', 'https://vkusvill.ru/goods/gruzinskiy-sup-iz-fasoli-s-ovoshchami-54161.html', 211, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/gruzinskiy-sup-iz-fasoli-s-ovoshchami-54161.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Окорочок куриный, запечённый, вес СП', 'https://vkusvill.ru/goods/okorochok-kurinyy-zapechyennyy-ves-sp-107621.html', 1, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/okorochok-kurinyy-zapechyennyy-ves-sp-107621.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Салат мимоза, 600 г', 'https://vkusvill.ru/goods/salat-mimoza-600-g-40531.html', 650, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/salat-mimoza-600-g-40531.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Комбо: Лосось стейк с кожей на пару и салат Витаминный с лимонной заправкой', 'https://vkusvill.ru/goods/kombo-losos-steyk-s-kozhey-na-paru-i-salat-vitaminnyy-s-limonnoy-zapravkoy-98448.html', 995, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kombo-losos-steyk-s-kozhey-na-paru-i-salat-vitaminnyy-s-limonnoy-zapravkoy-98448.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Английский завтрак', 'https://vkusvill.ru/goods/angliyskiy-zavtrak-53931.html', 465, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/angliyskiy-zavtrak-53931.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Навага жареная, вес', 'https://vkusvill.ru/goods/navaga-zharenaya-ves-54399.html', 755, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/navaga-zharenaya-ves-54399.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Гречка с котлетами из индейки', 'https://vkusvill.ru/goods/grechka-s-kotletami-iz-indeyki-46647.html', 298, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/grechka-s-kotletami-iz-indeyki-46647.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Паста Орзо с креветками', 'https://vkusvill.ru/goods/pasta-orzo-s-krevetkami-78100.html', 303, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/pasta-orzo-s-krevetkami-78100.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Крем-суп тыквенный с сухариками', 'https://vkusvill.ru/goods/krem-sup-tykvennyy-s-sukharikami-67056.html', 275, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/krem-sup-tykvennyy-s-sukharikami-67056.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Гречка отварная с овощами, 200 г', 'https://vkusvill.ru/goods/grechka-otvarnaya-s-ovoshchami-200-g-61751.html', 148, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/grechka-otvarnaya-s-ovoshchami-200-g-61751.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Чиабатта с жюльеном из курицы и грибов', 'https://vkusvill.ru/goods/chiabatta-s-zhyulenom-iz-kuritsy-i-gribov-115004.html', 288, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/chiabatta-s-zhyulenom-iz-kuritsy-i-gribov-115004.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Каша из полбы на кокосовом молоке', 'https://vkusvill.ru/goods/kasha-iz-polby-na-kokosovom-moloke-94983.html', 204, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kasha-iz-polby-na-kokosovom-moloke-94983.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Пицца "Курица и грибы"', 'https://vkusvill.ru/goods/pitstsa-kuritsa-i-griby-109440.html', 488, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/pitstsa-kuritsa-i-griby-109440.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Салат "Греческий"', 'https://vkusvill.ru/goods/salat-grecheskiy-99650.html', 298, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/salat-grecheskiy-99650.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Салат "Оливье" с колбасой, 200 грамм', 'https://vkusvill.ru/goods/salat-olive-s-kolbasoy-200-gramm-68093.html', 198, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/salat-olive-s-kolbasoy-200-gramm-68093.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Салат "Оливье вегетарианский"', 'https://vkusvill.ru/goods/salat-olive-vegetarianskiy-45767.html', 228, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/salat-olive-vegetarianskiy-45767.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Салат "Цезарь"', 'https://vkusvill.ru/goods/salat-tsezar-19363.html', 328, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/salat-tsezar-19363.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Блины с яблоком и корицей', 'https://vkusvill.ru/goods/bliny-s-yablokom-i-koritsey-94841.html', 228, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/bliny-s-yablokom-i-koritsey-94841.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Макароны с тушёной говядиной', 'https://vkusvill.ru/goods/makarony-s-tushyenoy-govyadinoy-106904.html', 268, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/makarony-s-tushyenoy-govyadinoy-106904.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Блины пшеничные, 200 г', 'https://vkusvill.ru/goods/bliny-pshenichnye-200-g-105061.html', 188, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/bliny-pshenichnye-200-g-105061.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Блины пшеничные', 'https://vkusvill.ru/goods/bliny-pshenichnye-19345.html', 188, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/bliny-pshenichnye-19345.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Утка конфи с картофельным пюре и сливовым соусом', 'https://vkusvill.ru/goods/utka-konfi-s-kartofelnym-pyure-i-slivovym-sousom-116207.html', 578, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/utka-konfi-s-kartofelnym-pyure-i-slivovym-sousom-116207.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Биточки из курицы и капусты, 480 г', 'https://vkusvill.ru/goods/bitochki-iz-kuritsy-i-kapusty-480-g-76795.html', 490, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/bitochki-iz-kuritsy-i-kapusty-480-g-76795.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Яичница глазунья с пикантной сосиской и томатами с зерновым хлебом на гриле', 'https://vkusvill.ru/goods/yaichnitsa-glazunya-s-pikantnoy-sosiskoy-i-tomatami-s-zernovym-khlebom-na-grile-88230.html', 360, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/yaichnitsa-glazunya-s-pikantnoy-sosiskoy-i-tomatami-s-zernovym-khlebom-na-grile-88230.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Пиццета с ветчиной и сыром', 'https://vkusvill.ru/goods/pitstseta-s-vetchinoy-i-syrom-110281.html', 198, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/pitstseta-s-vetchinoy-i-syrom-110281.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Студень Домашний', 'https://vkusvill.ru/goods/studen-domashniy-75757.html', 244, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/studen-domashniy-75757.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Сибас тушка на пару, шт', 'https://vkusvill.ru/goods/sibas-tushka-na-paru-sht-90825.html', 645, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/sibas-tushka-na-paru-sht-90825.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Куриное филе в кляре с рисом и кисло-сладким соусом', 'https://vkusvill.ru/goods/kurinoe-file-v-klyare-s-risom-i-kislo-sladkim-sousom-319127.html', 276, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kurinoe-file-v-klyare-s-risom-i-kislo-sladkim-sousom-319127.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Салат "Филадельфия"', 'https://vkusvill.ru/goods/salat-filadelfiya-40527.html', 298, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/salat-filadelfiya-40527.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Похлебка грибная', 'https://vkusvill.ru/goods/pokhlebka-gribnaya-24796.html', 188, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/pokhlebka-gribnaya-24796.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Лагман жареный с говядиной', 'https://vkusvill.ru/goods/lagman-zharenyy-s-govyadinoy-83776.html', 252, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/lagman-zharenyy-s-govyadinoy-83776.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Комбо: Лосось филе с кожей на пару и салат винегрет без картофеля', 'https://vkusvill.ru/goods/kombo-losos-file-s-kozhey-na-paru-i-salat-vinegret-bez-kartofelya-98452.html', 995, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kombo-losos-file-s-kozhey-na-paru-i-salat-vinegret-bez-kartofelya-98452.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Чебуреки с сыром, 140 г', 'https://vkusvill.ru/goods/chebureki-s-syrom-140-g-68197.html', 261, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/chebureki-s-syrom-140-g-68197.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Онигири с угрем и творожным сыром', 'https://vkusvill.ru/goods/onigiri-s-ugrem-i-tvorozhnym-syrom-101080.html', 206, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/onigiri-s-ugrem-i-tvorozhnym-syrom-101080.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Жаркое, вес СП', 'https://vkusvill.ru/goods/zharkoe-ves-sp-107667.html', 1, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/zharkoe-ves-sp-107667.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Куриные котлеты с пюре и сырным соусом', 'https://vkusvill.ru/goods/kurinye-kotlety-s-pyure-i-syrnym-sousom-94926.html', 300, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kurinye-kotlety-s-pyure-i-syrnym-sousom-94926.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Пирожок постный с яблоком, кафе рег', 'https://vkusvill.ru/goods/pirozhok-postnyy-s-yablokom-kafe-reg-100955.html', 86, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/pirozhok-postnyy-s-yablokom-kafe-reg-100955.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Говядина тушеная с лисичками и пастой Ризо в сливочно-грибном соусе', 'https://vkusvill.ru/goods/govyadina-tushenaya-s-lisichkami-i-pastoy-rizo-v-slivochno-gribnom-souse-101030.html', 388, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/govyadina-tushenaya-s-lisichkami-i-pastoy-rizo-v-slivochno-gribnom-souse-101030.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Парфе-гранола с голубикой, клубникой и йогуртом', 'https://vkusvill.ru/goods/parfe-granola-s-golubikoy-klubnikoy-i-yogurtom-53266.html', 360, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/parfe-granola-s-golubikoy-klubnikoy-i-yogurtom-53266.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Майами ролл', 'https://vkusvill.ru/goods/mayami-roll-379129.html', 575, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/mayami-roll-379129.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Мама бургер с голубым сыром и вишневым вареньем', 'https://vkusvill.ru/goods/mama-burger-s-golubym-syrom-i-vishnevym-varenem-43138.html', 560, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/mama-burger-s-golubym-syrom-i-vishnevym-varenem-43138.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Сырный Хот-Дог Барбекю', 'https://vkusvill.ru/goods/syrnyy-khot-dog-barbekyu-116569.html', 410, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/syrnyy-khot-dog-barbekyu-116569.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Салат "Чафан" с курицей', 'https://vkusvill.ru/goods/salat-chafan-s-kuritsey-116433.html', 298, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/salat-chafan-s-kuritsey-116433.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Тортилья с мортаделлой, сыром и песто, кафе', 'https://vkusvill.ru/goods/tortilya-s-mortadelloy-syrom-i-pesto-kafe-114256.html', 256, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/tortilya-s-mortadelloy-syrom-i-pesto-kafe-114256.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Шаурма с креветкой, соусом тысяча островов и салатом Романо', 'https://vkusvill.ru/goods/shaurma-s-krevetkoy-sousom-tysyacha-ostrovov-i-salatom-romano-99925.html', 368, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/shaurma-s-krevetkoy-sousom-tysyacha-ostrovov-i-salatom-romano-99925.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Ежики мясные с отварной гречкой', 'https://vkusvill.ru/goods/ezhiki-myasnye-s-otvarnoy-grechkoy-43719.html', 248, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/ezhiki-myasnye-s-otvarnoy-grechkoy-43719.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Котлета куриная "Солнышко" с картофельным пюре ДК', 'https://vkusvill.ru/goods/kotleta-kurinaya-solnyshko-s-kartofelnym-pyure-dk-64022.html', 355, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kotleta-kurinaya-solnyshko-s-kartofelnym-pyure-dk-64022.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Котлетки рыбные с запеченными овощами', 'https://vkusvill.ru/goods/kotletki-rybnye-s-zapechennymi-ovoshchami-30084.html', 298, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kotletki-rybnye-s-zapechennymi-ovoshchami-30084.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Кесадилья с ветчиной из индейки и сыром', 'https://vkusvill.ru/goods/kesadilya-s-vetchinoy-iz-indeyki-i-syrom-83595.html', 348, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kesadilya-s-vetchinoy-iz-indeyki-i-syrom-83595.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Киш с курицей и грибами, кафе', 'https://vkusvill.ru/goods/kish-s-kuritsey-i-gribami-kafe-27158.html', 262, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kish-s-kuritsey-i-gribami-kafe-27158.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Сэндвич ролл с горбушей и яйцом', 'https://vkusvill.ru/goods/sendvich-roll-s-gorbushey-i-yaytsom-22664.html', 248, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/sendvich-roll-s-gorbushey-i-yaytsom-22664.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Закуска "брокколи по-корейски"', 'https://vkusvill.ru/goods/zakuska-brokkoli-po-koreyski-113678.html', 278, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/zakuska-brokkoli-po-koreyski-113678.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Шницель куриный в яйце, 360 г', 'https://vkusvill.ru/goods/shnitsel-kurinyy-v-yaytse-360-g-67887.html', 488, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/shnitsel-kurinyy-v-yaytse-360-g-67887.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Бейгл с форелью и творожным сыром, кафе', 'https://vkusvill.ru/goods/beygl-s-forelyu-i-tvorozhnym-syrom-kafe-79221.html', 326, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/beygl-s-forelyu-i-tvorozhnym-syrom-kafe-79221.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Плов с курицей, Халяль', 'https://vkusvill.ru/goods/plov-s-kuritsey-khalyal-94476.html', 268, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/plov-s-kuritsey-khalyal-94476.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Долма с мясом и рисом по-грузински', 'https://vkusvill.ru/goods/dolma-s-myasom-i-risom-po-gruzinski-114875.html', 358, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/dolma-s-myasom-i-risom-po-gruzinski-114875.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Онигири с семгой холодного копчения', 'https://vkusvill.ru/goods/onigiri-s-semgoy-kholodnogo-kopcheniya-88407.html', 218, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/onigiri-s-semgoy-kholodnogo-kopcheniya-88407.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Большая порция сырников с малиной', 'https://vkusvill.ru/goods/bolshaya-portsiya-syrnikov-s-malinoy-52814.html', 515, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/bolshaya-portsiya-syrnikov-s-malinoy-52814.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Суши-салат с крабовым соусом', 'https://vkusvill.ru/goods/sushi-salat-s-krabovym-sousom-338320.html', 445, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/sushi-salat-s-krabovym-sousom-338320.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Говяжьи тефтели в томатном соусе с гречкой и грибами', 'https://vkusvill.ru/goods/govyazhi-tefteli-v-tomatnom-souse-s-grechkoy-i-gribami-337565.html', 366, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/govyazhi-tefteli-v-tomatnom-souse-s-grechkoy-i-gribami-337565.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Котлеты куриные (2 шт) с картофельным пюре ДК', 'https://vkusvill.ru/goods/kotlety-kurinye-2-sht-s-kartofelnym-pyure-dk-115281.html', 338, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kotlety-kurinye-2-sht-s-kartofelnym-pyure-dk-115281.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Рис с кукурузой и бобами эдамаме, вес СП', 'https://vkusvill.ru/goods/ris-s-kukuruzoy-i-bobami-edamame-ves-sp-108341.html', 880, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/ris-s-kukuruzoy-i-bobami-edamame-ves-sp-108341.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Гранола ягодная с брусничной сгущёнкой', 'https://vkusvill.ru/goods/granola-yagodnaya-s-brusnichnoy-sgushchyenkoy-116578.html', 380, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/granola-yagodnaya-s-brusnichnoy-sgushchyenkoy-116578.html');
INSERT INTO product (name, url, price, currency, delivery_service_id)
SELECT 'Каша ячневая с яблоком и корицей', 'https://vkusvill.ru/goods/kasha-yachnevaya-s-yablokom-i-koritsey-113859.html', 195, 'RUB', (SELECT id FROM delivery_service WHERE code='VKUSVILL' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE url='https://vkusvill.ru/goods/kasha-yachnevaya-s-yablokom-i-koritsey-113859.html');

-- Variants (product_id by url)
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/nalistniki-konvertiki-so-slivochnym-syrom-i-file-foreli-115876.html' LIMIT 1), NULL, NULL, 287, 13.1, 19.5, 14.9;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/konvertiki-s-kuritsey-shpinatom-i-syrom-109601.html' LIMIT 1), NULL, NULL, 333, 14.1, 19.5, 25.3;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/shtrudel-s-vishney-i-yablokom-80132.html' LIMIT 1), NULL, NULL, 184, 2.5, 5.3, 31.7;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kartofelnye-dolki-s-myasnym-farshem-bekonom-i-marinovannymi-ogurtsami-kafe-111731.html' LIMIT 1), NULL, NULL, 196, 4.42, 14.1, 13.09;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/korn-dog-sochiz-264165.html' LIMIT 1), NULL, NULL, 235, 9.23, 9.4, 28.57;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/krevetki-v-tempure-258109.html' LIMIT 1), NULL, NULL, 202, 13.93, 4.19, 27.16;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kani-tempura-roll-258111.html' LIMIT 1), NULL, NULL, 223, 4.46, 9.47, 30.71;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/burger-s-govyadinoy-barbekyu-43103.html' LIMIT 1), NULL, NULL, 278, 10.91, 12.49, 8.6;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/sendvich-roll-s-indeykoy-i-klyukvoy-116366.html' LIMIT 1), NULL, NULL, 235, 8.8, 10.0, 27.6;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/zakuska-imam-bayaldy-iz-baklazhanov-23039.html' LIMIT 1), NULL, NULL, 105, 1.5, 7.0, 9.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/morskaya-kapusta-s-naturalnym-myasom-kamchatskogo-kraba-300g-104848.html' LIMIT 1), NULL, NULL, 127, 10.0, 8.4, 3.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kombo-losos-steyk-s-kozhey-v-marinade-na-grile-i-salat-vitaminnyy-s-limonnoy-zapravkoy-98435.html' LIMIT 1), NULL, NULL, NULL, NULL, NULL, NULL;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-iz-svekly-s-chernoslivom-i-gretskim-orekhom-150-g-107373.html' LIMIT 1), NULL, NULL, 168, 2.7, 11.2, 14.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/sup-shchavelevyy-s-kuritsey-i-smetanoy-78990.html' LIMIT 1), NULL, NULL, 57, 4.1, 3.2, 3.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/khumus-ierusalimskiy-53716.html' LIMIT 1), NULL, NULL, 253, 7.3, 17.8, 16.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kurinye-serdechki-zharenye-s-lukom-95045.html' LIMIT 1), NULL, NULL, 217, 18.4, 14.8, 2.6;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kurinaya-grudka-su-vid-s-grechkoy-112683.html' LIMIT 1), NULL, NULL, 104, 11.5, 1.1, 12.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/yablochno-klubnichnyy-shtrudel-105328.html' LIMIT 1), NULL, NULL, 322, 3.7, 16.4, 39.9;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/rasstegai-s-lososem-i-paltusom-120-g-2-sht-46828.html' LIMIT 1), NULL, NULL, 249, 11.7, 11.8, 24.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/grechka-po-kupecheski-66903.html' LIMIT 1), NULL, NULL, 123, 5.5, 5.2, 13.7;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/derevenskiy-zavtrak-65057.html' LIMIT 1), NULL, NULL, 159, 6.51, 11.47, 7.95;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kartofel-mini-s-chesnokom-zelenyu-zapechennyy-100604.html' LIMIT 1), NULL, NULL, 102, 2.6, 1.3, 20.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/golubtsy-lenivye-iz-myasa-ptitsy-107205.html' LIMIT 1), NULL, NULL, 140, 9.1, 4.9, 15.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/assorti-zakusok-116101.html' LIMIT 1), 'Ролл с курицей', NULL, 239.3, 6.5, 16.9, 15.3;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/assorti-zakusok-116101.html' LIMIT 1), 'Рулетик с ветчиной', NULL, 300.5, 15.1, 24.9, 4.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/assorti-zakusok-116101.html' LIMIT 1), 'Рулетик из баклажанов', NULL, 313.0, 7.8, 28.6, 6.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/buterbrod-s-tuntsom-i-bazilikom-156-g-86021.html' LIMIT 1), NULL, NULL, 240, 11.4, 9.5, 27.4;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/bolshaya-portsiya-blinchiki-s-varenoy-sgushchenkoy-96343.html' LIMIT 1), NULL, NULL, 235, 6.5, 7.7, 34.9;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/age-ebi-sandochi-258132.html' LIMIT 1), NULL, NULL, 195, 5.41, 5.34, 31.82;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/sendvich-kruassan-so-slivochnym-mussom-iz-kety-115877.html' LIMIT 1), NULL, NULL, 245, 8.0, 15.0, 19.5;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/sendvich-roll-s-kuritsey-tanduri-38114.html' LIMIT 1), NULL, NULL, 206, 7.2, 10.9, 19.8;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/roll-syake-tempura-258113.html' LIMIT 1), NULL, NULL, 223, 5.47, 8.54, 30.09;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/golubtsy-lenivye-v-smetannom-souse-29522.html' LIMIT 1), NULL, NULL, 110, 6.5, 5.7, 8.2;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/krem-sup-s-lisichkami-i-belymi-gribami-98575.html' LIMIT 1), NULL, NULL, 147, 5.2, 9.0, 11.4;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/sendvich-roll-latinoamerikanskiy-s-sousom-ranch-114713.html' LIMIT 1), NULL, NULL, 317, 9.6, 19.4, 26.2;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/khot-dog-klassicheskiy-70429.html' LIMIT 1), NULL, NULL, 249, 8.8, 13.15, 24.12;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/gruzinskiy-sup-iz-fasoli-s-ovoshchami-54161.html' LIMIT 1), NULL, NULL, 45, 1.8, 1.5, 6.2;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/okorochok-kurinyy-zapechyennyy-ves-sp-107621.html' LIMIT 1), NULL, NULL, 324, 27.34, 23.54, 0.93;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-mimoza-600-g-40531.html' LIMIT 1), NULL, NULL, 218, 6.8, 18.1, 7.2;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kombo-losos-steyk-s-kozhey-na-paru-i-salat-vitaminnyy-s-limonnoy-zapravkoy-98448.html' LIMIT 1), NULL, NULL, NULL, NULL, NULL, NULL;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/angliyskiy-zavtrak-53931.html' LIMIT 1), NULL, NULL, 197, 8.39, 13.4, 11.53;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/navaga-zharenaya-ves-54399.html' LIMIT 1), NULL, NULL, 149, 14.4, 9.0, 2.5;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/grechka-s-kotletami-iz-indeyki-46647.html' LIMIT 1), NULL, NULL, 154, 12.7, 5.4, 13.8;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/pasta-orzo-s-krevetkami-78100.html' LIMIT 1), NULL, NULL, 165, 6.1, 7.5, 18.5;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/krem-sup-tykvennyy-s-sukharikami-67056.html' LIMIT 1), NULL, NULL, 47, 1.2, 2.5, 5.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/grechka-otvarnaya-s-ovoshchami-200-g-61751.html' LIMIT 1), NULL, NULL, 150, 4.1, 4.7, 23.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/chiabatta-s-zhyulenom-iz-kuritsy-i-gribov-115004.html' LIMIT 1), NULL, NULL, 328, 10.7, 20.4, 25.4;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kasha-iz-polby-na-kokosovom-moloke-94983.html' LIMIT 1), NULL, NULL, 137, 3.0, 5.0, 20.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/pitstsa-kuritsa-i-griby-109440.html' LIMIT 1), NULL, NULL, 251, 10.2, 11.8, 26.2;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-grecheskiy-99650.html' LIMIT 1), NULL, NULL, 56, 3.3, 3.6, 2.7;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-olive-s-kolbasoy-200-gramm-68093.html' LIMIT 1), NULL, NULL, 161, 4.0, 13.2, 6.6;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-olive-vegetarianskiy-45767.html' LIMIT 1), NULL, NULL, 117, 1.7, 8.7, 8.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-tsezar-19363.html' LIMIT 1), 'ФЛАЙ СЕРВИС ООО', NULL, 207.1, 12.2, 14.3, 7.4;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-tsezar-19363.html' LIMIT 1), 'СМАРТ ФУД СОЛЮШНЗ ООО', NULL, 251.4, 17.2, 15.0, 11.9;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-tsezar-19363.html' LIMIT 1), 'ЧИКЕН ФЭКТОРИ ООО', NULL, 225.0, 12.7, 15.8, 8.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-tsezar-19363.html' LIMIT 1), 'ФАСТЛЭНД ООО', NULL, 141.5, 9.6, 9.1, 5.3;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-tsezar-19363.html' LIMIT 1), 'Корона Фуд ООО', NULL, 214.9, 7.1, 15.3, 12.2;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-tsezar-19363.html' LIMIT 1), 'ЛАВКА ДЭЙЛИ ООО', NULL, 223.0, 8.8, 16.6, 9.6;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-tsezar-19363.html' LIMIT 1), 'КОРС ООО', NULL, 156.8, 10.4, 7.6, 11.7;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/bliny-s-yablokom-i-koritsey-94841.html' LIMIT 1), NULL, NULL, 260, 8.0, 16.0, 21.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/makarony-s-tushyenoy-govyadinoy-106904.html' LIMIT 1), NULL, NULL, 203, 7.7, 6.7, 21.6;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/bliny-pshenichnye-200-g-105061.html' LIMIT 1), NULL, NULL, 294, 15.4, 10.4, 34.8;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/bliny-pshenichnye-19345.html' LIMIT 1), 'ГУРМАН ООО', NULL, 243.1, 10.5, 9.1, 29.8;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/bliny-pshenichnye-19345.html' LIMIT 1), 'СМИК ООО', NULL, 232.6, 6.6, 11.8, 25.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/bliny-pshenichnye-19345.html' LIMIT 1), 'ТПК НВН ООО', NULL, 277.1, 5.2, 11.9, 37.3;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/utka-konfi-s-kartofelnym-pyure-i-slivovym-sousom-116207.html' LIMIT 1), NULL, NULL, 284, 9.3, 23.2, 9.6;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/bitochki-iz-kuritsy-i-kapusty-480-g-76795.html' LIMIT 1), 'ЭКОФУД ООО', NULL, 151.0, 11.3, 9.8, 4.4;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/bitochki-iz-kuritsy-i-kapusty-480-g-76795.html' LIMIT 1), 'ЮНИОН-ФУД ООО', NULL, 125.7, 10.8, 6.9, 5.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/bitochki-iz-kuritsy-i-kapusty-480-g-76795.html' LIMIT 1), 'Корона Фуд ООО', NULL, 119.3, 13.9, 4.9, 4.9;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/yaichnitsa-glazunya-s-pikantnoy-sosiskoy-i-tomatami-s-zernovym-khlebom-na-grile-88230.html' LIMIT 1), NULL, NULL, 184, 12.19, 12.26, 6.46;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/pitstseta-s-vetchinoy-i-syrom-110281.html' LIMIT 1), NULL, NULL, 294, 13.7, 12.9, 31.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/studen-domashniy-75757.html' LIMIT 1), NULL, NULL, NULL, NULL, NULL, NULL;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/sibas-tushka-na-paru-sht-90825.html' LIMIT 1), NULL, NULL, NULL, NULL, NULL, NULL;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kurinoe-file-v-klyare-s-risom-i-kislo-sladkim-sousom-319127.html' LIMIT 1), NULL, NULL, 261, 4.75, 9.08, 40.27;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-filadelfiya-40527.html' LIMIT 1), 'ГЛОБАЛ ФУД ООО', NULL, 148.1, 5.3, 5.7, 18.9;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-filadelfiya-40527.html' LIMIT 1), 'МОРЕСАППЛАЙ ООО', NULL, 195.7, 2.3, 10.9, 19.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-filadelfiya-40527.html' LIMIT 1), 'СТУДИЯ ВКУСА ООО', NULL, 164.1, 5.3, 6.9, 20.2;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-filadelfiya-40527.html' LIMIT 1), 'РЕСТОРАН ДОМА КУЛИНАРНАЯ ФАБРИКА ООО', NULL, 166.9, 7.0, 6.5, 20.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/pokhlebka-gribnaya-24796.html' LIMIT 1), 'ГЛОБАЛ ФУД ООО', NULL, 24.2, 0.9, 0.2, 4.7;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/pokhlebka-gribnaya-24796.html' LIMIT 1), 'РЕСТОРАН ДОМА КУЛИНАРНАЯ ФАБРИКА ООО', NULL, 36.4, 0.8, 1.2, 5.6;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/pokhlebka-gribnaya-24796.html' LIMIT 1), 'СТУДИЯ ВКУСА ООО', NULL, 24.9, 0.8, 0.5, 4.3;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/pokhlebka-gribnaya-24796.html' LIMIT 1), 'ЮНИОН-ФУД ООО', NULL, 47.3, 1.5, 0.9, 8.3;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/lagman-zharenyy-s-govyadinoy-83776.html' LIMIT 1), NULL, NULL, 190, 9.2, 6.8, 23.2;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kombo-losos-file-s-kozhey-na-paru-i-salat-vinegret-bez-kartofelya-98452.html' LIMIT 1), NULL, NULL, NULL, NULL, NULL, NULL;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/chebureki-s-syrom-140-g-68197.html' LIMIT 1), NULL, NULL, 338, 15.2, 17.8, 29.4;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/onigiri-s-ugrem-i-tvorozhnym-syrom-101080.html' LIMIT 1), NULL, NULL, 175, 5.0, 5.3, 27.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/zharkoe-ves-sp-107667.html' LIMIT 1), NULL, NULL, 132, 5.94, 8.5, 8.16;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kurinye-kotlety-s-pyure-i-syrnym-sousom-94926.html' LIMIT 1), NULL, NULL, 190, 10.0, 12.0, 11.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/pirozhok-postnyy-s-yablokom-kafe-reg-100955.html' LIMIT 1), NULL, NULL, 233, 4.5, 2.7, 47.9;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/govyadina-tushenaya-s-lisichkami-i-pastoy-rizo-v-slivochno-gribnom-souse-101030.html' LIMIT 1), NULL, NULL, 213, 8.9, 10.9, 20.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/parfe-granola-s-golubikoy-klubnikoy-i-yogurtom-53266.html' LIMIT 1), NULL, NULL, 117, 6.59, 5.26, 11.49;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/mayami-roll-379129.html' LIMIT 1), NULL, NULL, 215, 2.59, 9.26, 29.97;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/mama-burger-s-golubym-syrom-i-vishnevym-varenem-43138.html' LIMIT 1), NULL, NULL, 316, 13.01, 22.32, 16.11;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/syrnyy-khot-dog-barbekyu-116569.html' LIMIT 1), NULL, NULL, 227, 13.68, 12.59, 14.81;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/salat-chafan-s-kuritsey-116433.html' LIMIT 1), NULL, NULL, 153, 4.4, 11.4, 8.2;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/tortilya-s-mortadelloy-syrom-i-pesto-kafe-114256.html' LIMIT 1), NULL, NULL, 283, 12.0, 15.0, 25.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/shaurma-s-krevetkoy-sousom-tysyacha-ostrovov-i-salatom-romano-99925.html' LIMIT 1), NULL, NULL, 178, 6.4, 6.1, 24.5;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/ezhiki-myasnye-s-otvarnoy-grechkoy-43719.html' LIMIT 1), NULL, NULL, 114, 7.2, 3.2, 14.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kotleta-kurinaya-solnyshko-s-kartofelnym-pyure-dk-64022.html' LIMIT 1), NULL, NULL, 195, 8.9, 12.4, 12.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kotletki-rybnye-s-zapechennymi-ovoshchami-30084.html' LIMIT 1), NULL, NULL, 95, 6.4, 5.0, 6.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kesadilya-s-vetchinoy-iz-indeyki-i-syrom-83595.html' LIMIT 1), NULL, NULL, 214, 13.7, 8.9, 19.9;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kish-s-kuritsey-i-gribami-kafe-27158.html' LIMIT 1), NULL, NULL, 273, 12.5, 16.9, 17.8;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/sendvich-roll-s-gorbushey-i-yaytsom-22664.html' LIMIT 1), 'МАФИЯ ВКУСА ООО', NULL, 247.2, 13.2, 12.4, 20.7;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/sendvich-roll-s-gorbushey-i-yaytsom-22664.html' LIMIT 1), 'МК ООО', NULL, 231.4, 6.5, 13.0, 22.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/sendvich-roll-s-gorbushey-i-yaytsom-22664.html' LIMIT 1), 'МОРЕСАППЛАЙ ООО/Усманова Елена Харисовна ИП', NULL, 225.1, 8.0, 11.5, 22.4;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/sendvich-roll-s-gorbushey-i-yaytsom-22664.html' LIMIT 1), 'МРС ООО', NULL, 241.4, 10.5, 12.2, 22.4;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/sendvich-roll-s-gorbushey-i-yaytsom-22664.html' LIMIT 1), 'СМАРТ ФУД СОЛЮШНЗ ООО', NULL, 227.5, 9.9, 11.5, 21.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/sendvich-roll-s-gorbushey-i-yaytsom-22664.html' LIMIT 1), 'Григорьева Татьяна Викторовна ИП', NULL, 225.1, 8.0, 11.5, 22.4;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/zakuska-brokkoli-po-koreyski-113678.html' LIMIT 1), NULL, NULL, 93, 2.6, 5.5, 8.3;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/shnitsel-kurinyy-v-yaytse-360-g-67887.html' LIMIT 1), 'Корона Фуд ООО', NULL, 180.0, 19.0, 8.0, 8.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/shnitsel-kurinyy-v-yaytse-360-g-67887.html' LIMIT 1), 'КУЛИНАРНАЯ ФАБРИКА "КОРОЛЕВСКИЙ ВКУС" ООО', NULL, 130.6, 17.7, 4.6, 4.6;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/shnitsel-kurinyy-v-yaytse-360-g-67887.html' LIMIT 1), 'ЭКОФУД ООО', NULL, 171.4, 16.9, 8.2, 7.5;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/beygl-s-forelyu-i-tvorozhnym-syrom-kafe-79221.html' LIMIT 1), NULL, NULL, 298, 15.0, 14.0, 28.0;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/plov-s-kuritsey-khalyal-94476.html' LIMIT 1), NULL, NULL, 217, 10.4, 11.5, 18.1;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/dolma-s-myasom-i-risom-po-gruzinski-114875.html' LIMIT 1), NULL, NULL, 186, 17.3, 10.1, 6.6;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/onigiri-s-semgoy-kholodnogo-kopcheniya-88407.html' LIMIT 1), NULL, NULL, 211, 7.2, 6.6, 30.8;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/bolshaya-portsiya-syrnikov-s-malinoy-52814.html' LIMIT 1), NULL, NULL, 223, 14.1, 8.6, 22.3;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/sushi-salat-s-krabovym-sousom-338320.html' LIMIT 1), NULL, NULL, 205, 3.37, 8.74, 27.88;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/govyazhi-tefteli-v-tomatnom-souse-s-grechkoy-i-gribami-337565.html' LIMIT 1), NULL, NULL, 159, 8.26, 9.29, 11.18;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kotlety-kurinye-2-sht-s-kartofelnym-pyure-dk-115281.html' LIMIT 1), NULL, NULL, 166, 6.44, 9.61, 13.45;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/ris-s-kukuruzoy-i-bobami-edamame-ves-sp-108341.html' LIMIT 1), NULL, NULL, 150, 4.7, 1.5, 29.7;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/granola-yagodnaya-s-brusnichnoy-sgushchyenkoy-116578.html' LIMIT 1), NULL, NULL, 133, 6.66, 5.52, 14.25;
INSERT INTO product_variant (product_id, manufacturer, composition, calories, protein, fat, carbs)
SELECT (SELECT id FROM product WHERE url='https://vkusvill.ru/goods/kasha-yachnevaya-s-yablokom-i-koritsey-113859.html' LIMIT 1), NULL, NULL, 61, 1.0, 1.2, 11.6;