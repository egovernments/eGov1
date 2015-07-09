/**
 * eGov suite of products aim to improve the internal efficiency,transparency, 
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation 
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or 
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

	1) All versions of this program, verbatim or modified must carry this 
	   Legal Notice.

	2) Any misrepresentation of the origin of the material is prohibited. It 
	   is required that all modified versions of this material be marked in 
	   reasonable ways as different from the original version.

	3) This license does not grant any rights to any user of the program 
	   with regards to rights under trademark law for use of the trade names 
	   or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.commons.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.CChartOfAccounts;
import org.egov.exceptions.EGOVException;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infstr.dao.GenericHibernateDAO;
import org.egov.infstr.utils.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository
public class ChartOfAccountsHibernateDAO extends GenericHibernateDAO implements ChartOfAccountsDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Session  getCurrentSession() {
            return entityManager.unwrap(Session.class);
    }
    
    private final static Logger LOG = Logger.getLogger(ChartOfAccountsHibernateDAO.class);
    
    public ChartOfAccountsHibernateDAO(final Class persistentClass, final Session session) {
        super(persistentClass, session);
    }
    
    public ChartOfAccountsHibernateDAO(){
    	super(CChartOfAccounts.class,null);
    }
    
    @Deprecated
    public Collection getAccountCodeListForDetails() {
        return HibernateUtil.getCurrentSession().createQuery("select acc from CChartOfAccounts acc where acc.classification='4' and acc.isActiveForPosting = 1 order by acc.glcode").list();
    }
    
    /**
     * This API will give the list of detailed active for posting chartofaccounts list
     * @return
     * @throws EGOVException
     */
    public List<CChartOfAccounts> getDetailedAccountCodeList() {
        return HibernateUtil.getCurrentSession().createQuery("select acc from CChartOfAccounts acc where acc.classification='4' and acc.isActiveForPosting = 1 order by acc.glcode").setCacheable(true).list();
    }
    
    @Deprecated
    public CChartOfAccounts findCodeByPurposeId(final int purposeId) {
        final Query qry = HibernateUtil.getCurrentSession().createQuery("select acc from CChartOfAccounts acc where acc.purposeId=:purposeId ");
        qry.setLong("purposeId", purposeId);
        return (CChartOfAccounts) qry.uniqueResult();
    }
    
    public CChartOfAccounts getCChartOfAccountsByGlCode(final String glCode) {
        final Query qry = HibernateUtil.getCurrentSession().createQuery("from CChartOfAccounts coa where coa.glcode =:glCode");
        qry.setString("glCode", glCode);
        qry.setCacheable(true);
        return (CChartOfAccounts) qry.uniqueResult();
    }
    
    @Deprecated
    public List getChartOfAccountsForTds() {
        final Query qry = HibernateUtil.getCurrentSession().createQuery("from CChartOfAccounts coa where purposeId = 10 order by glcode");
        return qry.list();
    }
    
    @Deprecated
    public int getDetailTypeId(final String glCode, final Connection connection) throws Exception {
        int detailTypeId=0;
			ResultSet rs;
			String qryDetailType="Select detailtypeid from chartofaccountdetail where glcodeid=(select id from chartofaccounts where glcode=?)";
			PreparedStatement st=connection.prepareStatement(qryDetailType);
			st.setString(1, glCode);
			rs=st.executeQuery();
			if(rs.next())
			{
				detailTypeId=rs.getInt(1);
			}
			rs.close();
			st.close();
			return detailTypeId;
    }
    
    @Deprecated
    public int getDetailTypeIdByName(final String glCode, final Connection connection, final String name) {
		final SQLQuery query = HibernateUtil.getCurrentSession().createSQLQuery(
				"SELECT a.ID FROM accountdetailtype a,chartofaccountdetail coad  WHERE coad.DETAILTYPEID =a.ID  AND coad.glcodeid=(SELECT ID FROM chartofaccounts WHERE glcode=:glCode) AND a.NAME=:name");
		query.setString("glCode", glCode);
		query.setString("name", name);
		 List accountDtlTypeList = query.list();
			return (accountDtlTypeList != null) && (accountDtlTypeList.size() != 0) ? Integer.valueOf(accountDtlTypeList.get(0).toString()) : 0;
	}
    
    /**
     * This API will return the accountdetailtype for an account code when the accountcode and the
     * respective accountdetailtype name is passed.
     * @param glcode - This the chartofaccount code (mandatory)
     * @param name - This is the accountdetailtype name that is associated with the account code
     * (mandatory)
     * @return - Returns the accountdetailtype object if the account code is having the passed
     * accountdetailtype name, else NULL
     */
    public Accountdetailtype getAccountDetailTypeIdByName(final String glCode, final String name) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(glCode)) {
            throw new EGOVRuntimeException("Account Code or Account Detail Type Name is empty");
        }
        Query query = HibernateUtil.getCurrentSession().createQuery("from CChartOfAccounts where glcode=:glCode");
        query.setString("glCode", glCode);
        if (query.list().isEmpty()) {
            throw new EGOVRuntimeException("GL Code not found in Chart of Accounts");
        }
        query = HibernateUtil.getCurrentSession().createQuery("from Accountdetailtype where id in (select cd.detailTypeId from " +
        		"CChartOfAccountDetail  as cd,CChartOfAccounts as c where cd.glCodeId=c.id and c.glcode=:glCode) and name=:name");
        query.setString("glCode", glCode);
        query.setString("name", name);
        return (Accountdetailtype) query.uniqueResult();
    }
    
    public List getGlcode(final String minGlcode, final String maxGlcode, final String majGlcode) {
        Query qry = null;
        final StringBuilder qryStr = new StringBuilder("select coa.glcode from CChartOfAccounts coa where ");
        if (StringUtils.isNotBlank(minGlcode) && StringUtils.isNotBlank(maxGlcode)) {
            qryStr.append(" coa.glcode between :minGlcode and :maxGlcode ");
            qry = HibernateUtil.getCurrentSession().createQuery(qryStr.toString());
            qry.setString("minGlcode", minGlcode + "%");
            qry.setString("maxGlcode", maxGlcode + "%");
        } else if (StringUtils.isNotBlank(maxGlcode)) {
            qryStr.append(" coa.glcode like :maxGlcode ");
            qry = HibernateUtil.getCurrentSession().createQuery(qryStr.toString());
            qry.setString("maxGlcode", maxGlcode + "%");
        } else if (StringUtils.isNotBlank(majGlcode)) {
            qryStr.append(" coa.glcode =:majGlcode ");
            qry = HibernateUtil.getCurrentSession().createQuery(qryStr.toString());
            qry.setString("majGlcode", majGlcode);
        }
        return qry == null ? null : qry.list();
    }
    
    /**
     * This API will return the list of detailed chartofaccounts objects that are active for posting
     * for the Type.
     * @param -Accounting type-(Asset (A), Liability (L), Income (I), Expense (E))
     * @return list of chartofaccount objects
     */
    public List<CChartOfAccounts> getActiveAccountsForType(final char type) {
        final Query query = HibernateUtil.getCurrentSession().createQuery("select acc from CChartOfAccounts acc where acc.classification='4' and acc.isActiveForPosting = 1 and type=:type order by acc.name");
        query.setCharacter("type", type);
        return query.list();
    }
    
    /**
     * to get the list of chartofaccounts based on the purposeId. First query will get the detail
     * codes for the purpose is mapped to major code level. second query will get the detail codes
     * for the purpose is mapped to minor code level. last one will get the detail codes are mapped
     * to the detail code level.
     * @param purposeId
     * @return list of COA object(s)
     */
    public List<CChartOfAccounts> getAccountCodeByPurpose(final Integer purposeId) {
        final List<CChartOfAccounts> accountCodeList = new ArrayList<CChartOfAccounts>();
        try {
            if ((purposeId == null) || (purposeId.intValue() == 0)) {
                throw new EGOVException("Purpose Id is null or zero");
            }
            Query query = HibernateUtil.getCurrentSession().createQuery(" from EgfAccountcodePurpose purpose where purpose.id=:purposeId");
            query.setInteger("purposeId", purposeId);
            if (query.uniqueResult() == null) {
                throw new EGOVException("Purpose ID provided is not defined in the system");
            }
            query = HibernateUtil.getCurrentSession().createQuery(" FROM CChartOfAccounts WHERE parentId IN (SELECT id FROM CChartOfAccounts WHERE parentId IN (SELECT id FROM CChartOfAccounts WHERE parentId IN (SELECT id FROM CChartOfAccounts WHERE purposeid=:purposeId))) AND classification=4 AND isActiveForPosting=1 ");
            query.setLong("purposeId", purposeId);
            query.setCacheable(true);
            accountCodeList.addAll(query.list());
            query = HibernateUtil.getCurrentSession().createQuery(" FROM CChartOfAccounts WHERE parentId IN (SELECT id FROM CChartOfAccounts WHERE parentId IN (SELECT id FROM CChartOfAccounts WHERE purposeid=:purposeId)) AND classification=4 AND isActiveForPosting=1 ");
            query.setLong("purposeId", purposeId);
            query.setCacheable(true);
            accountCodeList.addAll(query.list());
            query = HibernateUtil.getCurrentSession().createQuery(" FROM CChartOfAccounts WHERE parentId IN (SELECT id FROM CChartOfAccounts WHERE purposeid=:purposeId) AND classification=4 AND isActiveForPosting=1 ");
            query.setLong("purposeId", purposeId);
            query.setCacheable(true);
            accountCodeList.addAll(query.list());
            query = HibernateUtil.getCurrentSession().createQuery(" FROM CChartOfAccounts WHERE purposeid=:purposeId AND classification=4 AND isActiveForPosting=1 ");
            query.setLong("purposeId", purposeId);
            query.setCacheable(true);
            accountCodeList.addAll(query.list());
        } catch (final Exception e) {
            LOG.error(e);
            throw new EGOVRuntimeException("Error occurred while getting Account Code by purpose", e);
        }
        return accountCodeList;
    }
    
    /**
     * This API will return the list of non control detailed chartofaccount codes that are active
     * for posting.
     * @return list of chartofaccount objects.
     */
    public List<CChartOfAccounts> getNonControlCodeList() {
        try {
            return HibernateUtil.getCurrentSession().createQuery(" from CChartOfAccounts acc where acc.classification=4 and acc.isActiveForPosting=1 and acc.id not in (select cd.glCodeId from CChartOfAccountDetail cd) ").list();
        } catch (final Exception e) {
            LOG.error(e);
            throw new EGOVRuntimeException("Error occurred while getting Non-Control Code list", e);
        }
    }
    
    /**
     * @description- This method returns a list of detail type object based on the glcode.
     * @param glCode - glcode supplied by the client.
     * @return List<Accountdetailtype> -list of Accountdetailtype object(s).
     * @throws EGOVException
     */
    @SuppressWarnings("unchecked")
    public List<Accountdetailtype> getAccountdetailtypeListByGLCode(final String glCode) {
        if (StringUtils.isBlank(glCode)) {
            throw new EGOVRuntimeException("GL Code is empty ");
        }
        // checking if the glcode is exists in ChartOfAccounts table.
       
        CChartOfAccounts cChartOfAccountsByGlCode = getCChartOfAccountsByGlCode(glCode);
        if (cChartOfAccountsByGlCode==null) {
            throw new EGOVRuntimeException("GL Code not found in Chart of Accounts");
        }
        try {
            Query query = HibernateUtil.getCurrentSession().createQuery("from Accountdetailtype where id in (select cd.detailTypeId " +
            		"from CChartOfAccountDetail  as cd,CChartOfAccounts as c where cd.glCodeId=c.id and c.glcode=:glCode)");
            query.setString("glCode", glCode);
            query.setCacheable(true);
            return query.list().isEmpty() ? null : query.list(); // NOPMD
        } catch (final Exception e) {
            LOG.error(e);
            throw new EGOVRuntimeException("Error occured while getting Account Detail Types for GL Code ", e);
        }
    }
    
    /**
     * @author manoranjan
     * @description -Get list of COA for a list of types.
     * @param type - list of types,e.g income, Assets etc.
     * @return listChartOfAcc - list of chartofaccounts based on the given list of types
     * @throws ValidationException
     */
    public List<CChartOfAccounts> getActiveAccountsForTypes(final char[] type) throws ValidationException {
        if ((null == type) || (type.length == 0)) {
            throw new ValidationException(Arrays.asList(new ValidationError("type", "The supplied value for Chart of Account Type  can not be null or empty")));
        }
        final Character[] types = new Character[type.length];
        int count = 0;
        for (final char typ : type) {
            types[count++] = typ;
        }
        final Query query = HibernateUtil.getCurrentSession().createQuery("from CChartOfAccounts where classification=4 " +
        		"and isActiveForPosting=1 and type in (:type)");
        query.setParameterList("type", types);
        query.setCacheable(true);
        return query.list();
    }
    
    /**
     * @author manoranjan
     * @description - Get list of Chartofaccount objects for a list of purpose ids
     * @param purposeId - list of purpose ids.
     * @return listChartOfAcc - list of chartofaccount objects for the given list of purpose id
     * @throws ValidationException
     */
    public List<CChartOfAccounts> getAccountCodeByListOfPurposeId(final Integer[] purposeId) throws ValidationException {
        if ((null == purposeId) || (purposeId.length == 0)) {
            throw new ValidationException(Arrays.asList(new ValidationError("purposeId", "The supplied purposeId  can not be null or empty")));
        }
        final List<CChartOfAccounts> listChartOfAcc = new ArrayList<CChartOfAccounts>();
        Query query = HibernateUtil.getCurrentSession().createQuery(" FROM CChartOfAccounts WHERE purposeid in(:purposeId)AND classification=4 AND isActiveForPosting=1 ");
        query.setParameterList("purposeId", purposeId);
        query.setCacheable(true);
        listChartOfAcc.addAll(query.list());
        
        query = HibernateUtil.getCurrentSession().createQuery(" from CChartOfAccounts where parentId IN (select id  FROM CChartOfAccounts WHERE purposeid in (:purposeId) ) AND classification=4 AND isActiveForPosting=1 ");
        query.setParameterList("purposeId", purposeId);
        query.setCacheable(true);
        listChartOfAcc.addAll(query.list());
        
        query = HibernateUtil.getCurrentSession().createQuery(" from CChartOfAccounts where   parentId IN (select id from CChartOfAccounts where parentId IN (select id  FROM CChartOfAccounts WHERE purposeid in (:purposeId))) AND classification=4 AND isActiveForPosting=1");
        query.setParameterList("purposeId", purposeId);
        query.setCacheable(true);
        listChartOfAcc.addAll(query.list());
                
        query = HibernateUtil.getCurrentSession().createQuery(" from CChartOfAccounts where   parentId IN (select id from  CChartOfAccounts where   parentId IN (select id from CChartOfAccounts where parentId IN (select id  FROM CChartOfAccounts WHERE purposeid in (:purposeId)))) AND classification=4 AND isActiveForPosting=1 ");
        query.setParameterList("purposeId", purposeId);
        query.setCacheable(true);
        listChartOfAcc.addAll(query.list());
        
        return listChartOfAcc;
    }
    
    /**
     * @author manoranjan
     * @description - This api will return the list of detailed chartofaccounts objects that are
     * active for posting.
     * @param glcode - The input is the chartofaccounts code.
     */
    public List<CChartOfAccounts> getListOfDetailCode(final String glCode) throws ValidationException {
        if (StringUtils.isBlank(glCode)) {
            throw new ValidationException(Arrays.asList(new ValidationError("glcode null", "the glcode value supplied can not be null or blank")));
        }
        Query query = HibernateUtil.getCurrentSession().createQuery("from CChartOfAccounts where glcode=:glCode");
        query.setString("glCode", glCode);
        query.setCacheable(true);
        if (query.list().isEmpty()) {
            throw new ValidationException(Arrays.asList(new ValidationError("glcode not exist", "The GL Code value supplied does not exist in the System")));
        }
        final List<CChartOfAccounts> listChartOfAcc = new ArrayList<CChartOfAccounts>();
        query = HibernateUtil.getCurrentSession().createQuery(" FROM CChartOfAccounts WHERE glcode=:glCode  AND classification=4 AND isActiveForPosting=1 ");
        query.setString("glCode", glCode);
        query.setCacheable(true);
        listChartOfAcc.addAll(query.list());
        query = HibernateUtil.getCurrentSession().createQuery(" from CChartOfAccounts where parentId IN (select id  FROM CChartOfAccounts WHERE glcode=:glCode) AND classification=4 AND isActiveForPosting=1 ");
        query.setString("glCode", glCode);
        query.setCacheable(true);
        listChartOfAcc.addAll(query.list());
        query = HibernateUtil.getCurrentSession().createQuery(" from CChartOfAccounts where parentId IN (select id from CChartOfAccounts where parentId IN ( select id  FROM CChartOfAccounts WHERE glcode=:glCode)) AND classification=4 AND isActiveForPosting=1 ");
        query.setString("glCode", glCode);
        query.setCacheable(true);
        listChartOfAcc.addAll(query.list());
        query = HibernateUtil.getCurrentSession().createQuery(" from CChartOfAccounts where parentId IN (select id from  CChartOfAccounts where   parentId IN (select id from CChartOfAccounts where parentId IN ( select id  FROM CChartOfAccounts WHERE glcode=:glCode)))AND classification=4 AND isActiveForPosting=1 ");
        query.setString("glCode", glCode);
        query.setCacheable(true);
        listChartOfAcc.addAll(query.list());
        return listChartOfAcc;
    }
    
	public List<CChartOfAccounts> getBankChartofAccountCodeList() {
        return HibernateUtil.getCurrentSession().createQuery("select chartofaccounts from Bankaccount").setCacheable(true).list();
    }
}

