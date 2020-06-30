package de.mw.spring.data.jpa.repository.projection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

class ProjectionByMetamodelAttributesTest {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    void toSelections_createsSelectionsByTypeFromRoot() throws Exception {
        var root = mock(Root.class);
        var singleAttr = mock(SingularAttribute.class);
        var pluralAttr = mock(PluralAttribute.class);
        var otherAttr = mock(Attribute.class);
        when(otherAttr.getName()).thenReturn("foo");
        var singleSel = mock(Path.class);
        var pluralSel = mock(Path.class);
        var otherSel = mock(Path.class);
        when(root.get(any(SingularAttribute.class))).thenReturn(singleSel);
        when(root.get(any(PluralAttribute.class))).thenReturn(pluralSel);
        when(root.get(any(String.class))).thenReturn(otherSel);
        var testee = new ProjectionByMetamodelAttributes(singleAttr, pluralAttr, otherAttr);
        
        
        var result = testee.toSelections(root, null, null);
        
        
        verify(root).get(singleAttr);
        verify(root).get(pluralAttr);
        verify(root).get("foo");
        assertThat(result).containsExactly(singleSel, pluralSel, otherSel);
    }

}
