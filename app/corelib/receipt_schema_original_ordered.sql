create table vat_serie
(
  vat_serie_id bigint       not null
    constraint vat_serie_pkey
      primary key,
  version      bigint,
  status       varchar(255) not null
);

alter table vat_serie
  owner to postgres;

create table vat
(
  vat_id       bigint           not null
    constraint vat_pkey
      primary key,
  version      bigint,
  vat          double precision not null,
  name         varchar(255)     not null,
  status       varchar(255)     not null,
  vat_serie_id bigint           not null
    constraint vat_serie_id_fk
      references vat_serie
);

alter table vat
  owner to postgres;

create table restaurant
(
  restaurant_id bigint not null
    constraint restaurant_pkey
      primary key,
  version bigint,
  companyzipcode varchar(255) not null,
  companycity varchar(255) not null,
  companystreet varchar(255) not null,
  companyname varchar(255) not null,
  companytaxpayerid varchar(255) not null,
  phonenumber varchar(255),
  receiptdisclaimer varchar(255),
  receiptgreet varchar(255),
  receiptnote varchar(255),
  restaurantzipcode varchar(255) not null,
  restaurantcity varchar(255) not null,
  restaurantstreet varchar(255) not null,
  restaurantname varchar(255) not null,
  socialmediainfo varchar(255),
  website varchar(255)
);

alter table restaurant owner to postgres;

create table product
(
  product_id bigint not null
    constraint product_pkey
      primary key,
  version bigint,
  vatlocal double precision not null
    constraint product_vatlocal_check
      check (vatlocal <= (100)::double precision),
  vattakeaway double precision not null
    constraint product_vattakeaway_check
      check (vattakeaway <= (100)::double precision),
  longname varchar(255) not null,
  minimumstock integer not null,
  ordernumber integer not null,
  purchaseprice integer not null,
  quantityunit varchar(255) not null,
  rapidcode integer not null
    constraint product_rapidcode_check
      check (rapidcode <= 999),
  saleprice integer not null,
  shortname varchar(20) not null,
  status varchar(255) not null,
  stockwindow integer not null,
  storagemultiplier double precision not null,
  type varchar(255) not null
);

alter table product owner to postgres;

create table recipe
(
  recipe_id bigint not null
    constraint recipe_pkey
      primary key,
  version bigint,
  quantitymultiplier double precision not null,
  component_product_id bigint not null
    constraint recipe_component_product_fkey
      references product,
  product_id bigint not null
    constraint recipe_product_fkey
      references product
);

alter table recipe owner to postgres;

create table stock
(
  stock_id bigint not null
    constraint stock_pkey
      primary key,
  version bigint,
  date timestamp,
  disposedquantity double precision not null,
  initialquantity double precision not null,
  inventoryquantity double precision not null,
  purchasedquantity double precision not null,
  soldquantity double precision not null,
  product_id bigint not null
    constraint stock_product_fkey
      references product
);

alter table stock owner to postgres;

create table product_category
(
  category_id bigint not null
    constraint product_category_pkey
      primary key,
  version bigint,
  name varchar(255) not null
    constraint product_category_unique_name
      unique,
  ordernumber integer not null,
  status varchar(255) not null,
  type varchar(255),
  parent_category_id bigint,
  product_product_id bigint
);

alter table product_category owner to postgres;

create table product_category_relations
(
  parent bigint not null,
  children bigint not null
    constraint product_category_unique_children
      unique
    constraint product_category_relations_fkey
      references product_category
);

alter table product_category_relations owner to postgres;

create table price_modifier
(
  price_modifier_id bigint not null
    constraint price_modifier_pkey
      primary key,
  version bigint,
  dayofweek integer,
  discountpercent double precision not null,
  enddate timestamp not null,
  endtime time,
  name varchar(255) not null,
  quantitylimit integer not null,
  repeatperiod varchar(255) not null,
  repeatperiodmultiplier integer not null,
  startdate timestamp not null,
  starttime time,
  type varchar(255) not null,
  product_category_id bigint not null
    constraint fkraq8cx6ft5is3hji9k2guie8r
      references product_category
);

alter table price_modifier owner to postgres;

create table "_table"
(
  table_id bigint not null
    constraint "_table_pkey"
      primary key,
  version bigint,
  capacity integer not null,
  coordinatex integer not null,
  coordinatey integer not null,
  dimensionx integer not null,
  dimensiony integer not null,
  guestcount integer not null,
  name varchar(255),
  note varchar(255),
  number integer
    constraint _table_number_unique
      unique
    constraint "_table_number_check"
      check (number >= 0),
  type varchar(255) not null,
  visible boolean not null,
  consumer_table_id bigint,
  host_table_id bigint,
  restaurant_id bigint not null
    constraint _table_restaurant_fkey
      references restaurant
);

alter table "_table" owner to postgres;

create table "_table_consumer_relations"
(
  consumer bigint not null,
  consumed bigint not null
    constraint uk_fw49vw57r8v3n931xipsq7ojk
      unique
    constraint fk8jerp4kyekdut0atpk8h6vd0k
      references "_table"
);

alter table "_table_consumer_relations" owner to postgres;

create table "_table_host_relations"
(
  host bigint not null,
  hosted bigint not null
    constraint uk_coay57vw8d2220yennuo2qa5n
      unique
    constraint fk2fwtogjrp786t8lvlc1n8d6m3
      references "_table"
);

alter table "_table_host_relations" owner to postgres;

create table reservation
(
  reservation_id bigint not null
    constraint reservation_pkey
      primary key,
  version bigint,
  date date not null,
  endtime timestamp,
  guestcount integer not null,
  name varchar(255) not null,
  note varchar(255),
  phonenumber varchar(255),
  starttime timestamp not null,
  tablenumber integer not null
    constraint reservation_tablenumber_check
      check (tablenumber >= 1),
  table_id bigint
    constraint resrvation_table_fkey
      references "_table"
);

alter table reservation owner to postgres;

create table receipt
(
  receipt_id bigint not null
    constraint receipt_pkey
      primary key,
  version bigint,
  taxnumber varchar(255),
  address varchar(255),
  name varchar(255),
  closuretime timestamp,
  discountpercent double precision not null,
  opentime timestamp not null,
  paymentmethod varchar(255) not null,
  status varchar(255) not null,
  sumpurchasegrossprice integer not null,
  sumpurchasenetprice integer not null,
  sumsalegrossprice integer not null,
  sumsalenetprice integer not null,
  type varchar(255) not null,
  usercode integer not null,
  vat_serie_id bigint not null,
  table_id bigint not null
    constraint receipt_table_fkey
      references "_table",
  sumsalegrossoriginalprice integer,
  sumsalenetoriginalprice integer,
  deliverytime timestamp,
  isdelivered boolean default true
);

alter table receipt owner to postgres;

alter table receipt owner to postgres;

create table receipt_record
(
  receipt_record_id bigint not null
    constraint receipt_record_pkey
      primary key,
  version bigint,
  vat double precision not null,
  absolutequantity double precision not null,
  discountpercent double precision not null
    constraint receipt_record_discountpercent_check
      check (discountpercent <= (100)::double precision),
  name varchar(255) not null,
  purchaseprice integer not null,
  saleprice integer not null,
  soldquantity double precision not null,
  type varchar(255) not null,
  receipt_id bigint not null
    constraint receipt_record_receipt_fkey
      references receipt,
  product_id bigint,
  originalsaleprice integer
);

alter table receipt_record owner to postgres;

create table receipt_record_created
(
  receipt_record_created_id bigint not null
    constraint receipt_record_created_pkey
      primary key,
  version bigint,
  created timestamp,
  receipt_record_id bigint not null
    constraint receipt_record_created_fkey
      references receipt_record
);

alter table receipt_record_created owner to postgres;

create table authuser
(
  authuser_id bigint not null
    constraint authuser_pkey
      primary key,
  version bigint,
  password varchar(255) not null,
  role varchar(255) not null,
  username varchar(255) not null
    constraint uk_8i9734g2gyoivsd7c9oub4wl
      unique
);

alter table authuser owner to postgres;

create table daily_closure
(
  daily_closure_id bigint not null
    constraint daily_closure_pkey
      primary key,
  version bigint,
  closuretime timestamp,
  discount double precision not null,
  markup double precision not null,
  numberofreceipts integer not null,
  profit integer not null,
  receiptaverage integer not null,
  sumpurchasegrosspricecash integer not null,
  sumpurchasegrosspricecoupon integer not null,
  sumpurchasegrosspricecreditcard integer not null,
  sumpurchasegrosspricetotal integer not null,
  sumpurchasenetpricecash integer not null,
  sumpurchasenetpricecoupon integer not null,
  sumpurchasenetpricecreditcard integer not null,
  sumpurchasenetpricetotal integer not null,
  sumsalegrosspricecash integer not null,
  sumsalegrosspricecoupon integer not null,
  sumsalegrosspricecreditcard integer not null,
  sumsalegrosspricetotal integer not null,
  sumsalenetpricecash integer not null,
  sumsalenetpricecoupon integer not null,
  sumsalenetpricecreditcard integer not null,
  sumsalenetpricetotal integer not null,
  restaurant_id bigint not null
    constraint daily_closure_restaurant_fkey
      references restaurant
);

alter table daily_closure owner to postgres;

